<template>
  <div class="app">
    <!-- XtxShortCut  => 快捷链接 -->
     <XtxShortCut></XtxShortCut>
<!-- XtxHeaderNav => 顶部导航 -->
 <XtxHeaderNav></XtxHeaderNav>
<!-- XtxBanner    => 轮播区域 -->
 <XtxBanner></XtxBanner>
<!-- XtxNewGoods  => 新鲜好物 -->
 <XtxNewGoods></XtxNewGoods>  
<!-- XtxHotBrand  => 热门品牌 -->
 <XtxHotBrand></XtxHotBrand>
<!-- XtxTopic     => 最新专题 -->
 <XtxTopic></XtxTopic>
<!-- XtxFooter    => 版权底部 -->
 <XtxFooter></XtxFooter>
  </div>

</template>

<script>
import XtxShortCut from './components/XtxShortCut.vue'
import XtxHeaderNav from './components/XtxHeaderNav.vue'
import XtxBanner from './components/XtxBanner.vue'
import XtxNewGoods from './components/XtxNewGoods.vue'
import XtxHotBrand from './components/XtxHotBrand.vue'
import XtxTopic from './components/XtxTopic.vue'
import XtxFooter from './components/XtxFooter.vue'

export default {
components:{
  XtxShortCut,
  XtxBanner,
  XtxHeaderNav,
  XtxNewGoods,
  XtxHotBrand,
  XtxTopic,
  XtxFooter,

}
}
</script>

<style>

</style>