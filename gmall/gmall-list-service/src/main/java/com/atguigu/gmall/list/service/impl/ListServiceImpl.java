package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.Aggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;
    @Autowired
    RedisUtil redisUtil;

    public static final String ES_INDEX="gmall";

    public static final String ES_TYPE="SkuInfo";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        // 保存数据
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult documentResult = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams,searchResult);
        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        int timesToEs = 10;
        Double hotScore = jedis.zincrby("hotScore", 1, "skuId" + skuId);
        if(hotScore%timesToEs==0){
            updateHotScore(skuId,Math.round(hotScore));
        }

    }

    private void updateHotScore(String skuId, long hotScore) {
        String updateJson="{\n" +
                "   \"doc\":{\n" +
                "     \"hotScore\":"+hotScore+"\n" +
                "   }\n" +
                "}";
        Update build = new Update.Builder(updateJson).index("gmall").type("SkuInfo").id(skuId).build();
        try{
            jestClient.execute(build);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //创建查询bulid
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(skuLsParams.getKeyword()!=null){
            MatchQueryBuilder ma = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(ma);
            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置高亮字段
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            // 将高亮结果放入查询器中
            searchSourceBuilder.highlight(highlightBuilder);
        }
        // 设置三级分类
        if (skuLsParams.getCatalog3Id()!=null){
            TermQueryBuilder catalog3Id = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(catalog3Id);
        }
        // 设置属性值
        if (skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String valueId = skuLsParams.getValueId()[i];
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置分页
        int form = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 设置按照热度
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        // 设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        String query = searchSourceBuilder.toString();
        System.out.println("query"+query);
        return query;
    }
    public SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();
        ArrayList<SkuLsInfo> skuLsInfoList = new ArrayList<>(skuLsParams.getPageSize());
        //获取sku列表
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            SkuLsInfo skuLsInfo = hit.source;
            if (hit.highlight!=null&&hit.highlight.size()>0){
                List<String> list = hit.highlight.get("skuName");
                //把带有高亮显示的字符串代替skuName
                String skuNameHI = list.get(0);
                skuLsInfo.setSkuName(skuNameHI);
            }
            skuLsInfoList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        skuLsResult.setTotal(searchResult.getTotal());
        //取出记录并计算总页数
        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);
        //取出涉及的属性值id
        List<String> attrValueIdList = new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if (groupby_attr!=null){
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add(bucket.getKey());
            }
            skuLsResult.setAttrValueIdList(attrValueIdList);
        }

        return skuLsResult;
    }

}








