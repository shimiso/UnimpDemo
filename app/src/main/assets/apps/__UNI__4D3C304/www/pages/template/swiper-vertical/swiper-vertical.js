"use weex:vue";

if (typeof Promise !== 'undefined' && !Promise.prototype.finally) {
  Promise.prototype.finally = function(callback) {
    const promise = this.constructor
    return this.then(
      value => promise.resolve(callback()).then(() => value),
      reason => promise.resolve(callback()).then(() => {
        throw reason
      })
    )
  }
};

if (typeof uni !== 'undefined' && uni && uni.requireGlobal) {
  const global = uni.requireGlobal()
  ArrayBuffer = global.ArrayBuffer
  Int8Array = global.Int8Array
  Uint8Array = global.Uint8Array
  Uint8ClampedArray = global.Uint8ClampedArray
  Int16Array = global.Int16Array
  Uint16Array = global.Uint16Array
  Int32Array = global.Int32Array
  Uint32Array = global.Uint32Array
  Float32Array = global.Float32Array
  Float64Array = global.Float64Array
  BigInt64Array = global.BigInt64Array
  BigUint64Array = global.BigUint64Array
};


(()=>{var m=Object.create;var h=Object.defineProperty;var I=Object.getOwnPropertyDescriptor;var w=Object.getOwnPropertyNames;var y=Object.getPrototypeOf,D=Object.prototype.hasOwnProperty;var L=(e,t)=>()=>(t||e((t={exports:{}}).exports,t),t.exports);var C=(e,t,s,n)=>{if(t&&typeof t=="object"||typeof t=="function")for(let i of w(t))!D.call(e,i)&&i!==s&&h(e,i,{get:()=>t[i],enumerable:!(n=I(t,i))||n.enumerable});return e};var N=(e,t,s)=>(s=e!=null?m(y(e)):{},C(t||!e||!e.__esModule?h(s,"default",{value:e,enumerable:!0}):s,e));var g=L(($,_)=>{_.exports=Vue});var b=Object.prototype.toString,c=e=>b.call(e),p=e=>c(e).slice(8,-1);function S(){return typeof __channelId__=="string"&&__channelId__}function T(e,t){switch(p(t)){case"Function":return"function() { [native code] }";default:return t}}function E(e,t,s){return S()?(s.push(t.replace("at ","uni-app:///")),console[e].apply(console,s)):s.map(function(i){let a=c(i).toLowerCase();if(["[object object]","[object array]","[object module]"].indexOf(a)!==-1)try{i="---BEGIN:JSON---"+JSON.stringify(i,T)+"---END:JSON---"}catch(r){i=a}else if(i===null)i="---NULL---";else if(i===void 0)i="---UNDEFINED---";else{let r=p(i).toUpperCase();r==="NUMBER"||r==="BOOLEAN"?i="---BEGIN:"+r+"---"+i+"---END:"+r+"---":i=String(i)}return i}).join("---COMMA---")+" "+t}function v(e,t,...s){let n=E(e,t,s);n&&console[e](n)}var o=N(g());var f=(e,t)=>{let s=e.__vccOpts||e;for(let[n,i]of t)s[n]=i;return s};var O={page:{"":{flex:1}},swiper:{"":{flex:1,backgroundColor:"#007AFF"}},"swiper-item":{"":{flex:1}},video:{"":{flex:1}}},V=[{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-01.mp4"},{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-02.mp4"},{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-03.mp4"},{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-01.mp4"},{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-02.mp4"},{src:"https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/hellouniapp/hello-nvue-swiper-vertical-03.mp4"}],k={data(){return{circular:!0,videoList:[{id:"video0",src:"",img:""},{id:"video1",src:"",img:""},{id:"video2",src:"",img:""}],videoDataList:[]}},onLoad(e){},onReady(){this.init(),this.getData()},methods:{init(){this._videoIndex=0,this._videoContextList=[];for(var e=0;e<this.videoList.length;e++)this._videoContextList.push(uni.createVideoContext("video"+e,this));this._videoDataIndex=0},getData(e){this.videoDataList=V,setTimeout(()=>{this.updateVideo(!0)},200)},onSwiperChange(e){let t=e.detail.current;if(t===this._videoIndex)return;let s=!1;t===0&&this._videoIndex===this.videoList.length-1?s=!0:t===this.videoList.length-1&&this._videoIndex===0?s=!1:t>this._videoIndex&&(s=!0),s?this._videoDataIndex++:this._videoDataIndex--,this._videoDataIndex<0?this._videoDataIndex=this.videoDataList.length-1:this._videoDataIndex>=this.videoDataList.length&&(this._videoDataIndex=0),this.circular=this._videoDataIndex!=0,this._videoIndex>=0&&(this._videoContextList[this._videoIndex].pause(),this._videoContextList[this._videoIndex].seek(0)),this._videoIndex=t,setTimeout(()=>{this.updateVideo(s)},200)},getNextIndex(e){let t=this._videoIndex+(e?1:-1);return t<0?this.videoList.length-1:t>=this.videoList.length?0:t},getNextDataIndex(e){let t=this._videoDataIndex+(e?1:-1);return t<0?this.videoDataList.length-1:t>=this.videoDataList.length?0:t},updateVideo(e){this.$set(this.videoList[this._videoIndex],"src",this.videoDataList[this._videoDataIndex].src),this.$set(this.videoList[this.getNextIndex(e)],"src",this.videoDataList[this.getNextDataIndex(e)].src),setTimeout(()=>{this._videoContextList[this._videoIndex].play()},200),v("log","at pages/template/swiper-vertical/swiper-vertical.nvue:139","v:"+this._videoIndex+" d:"+this._videoDataIndex+"; next v:"+this.getNextIndex(e)+" next d:"+this.getNextDataIndex(e))}}};function B(e,t,s,n,i,a){let r=(0,o.resolveComponent)("swiper-item"),x=(0,o.resolveComponent)("swiper");return(0,o.openBlock)(),(0,o.createElementBlock)("scroll-view",{scrollY:!0,showScrollbar:!0,enableBackToTop:!0,bubble:"true",style:{flexDirection:"column"}},[(0,o.createElementVNode)("view",{class:"page"},[(0,o.createVNode)(x,{class:"swiper",circular:i.circular,vertical:!0,onChange:a.onSwiperChange},{default:(0,o.withCtx)(()=>[((0,o.openBlock)(!0),(0,o.createElementBlock)(o.Fragment,null,(0,o.renderList)(i.videoList,d=>((0,o.openBlock)(),(0,o.createBlock)(r,{key:d.id},{default:(0,o.withCtx)(()=>[(0,o.createElementVNode)("u-video",{class:"video",id:d.id,ref_for:!0,ref:d.id,src:d.src,controls:!1,loop:!0,showCenterPlayBtn:!1},null,8,["id","src"])]),_:2},1024))),128))]),_:1},8,["circular","onChange"])])])}var l=f(k,[["render",B],["styles",[O]]]);var u=plus.webview.currentWebview();if(u){let e=parseInt(u.id),t="pages/template/swiper-vertical/swiper-vertical",s={};try{s=JSON.parse(u.__query__)}catch(i){}l.mpType="page";let n=Vue.createPageApp(l,{$store:getApp({allowDefault:!0}).$store,__pageId:e,__pagePath:t,__pageQuery:s});n.provide("__globalStyles",Vue.useCssStyles([...__uniConfig.styles,...l.styles||[]])),n.mount("#root")}})();
