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


(()=>{var b=Object.create;var m=Object.defineProperty;var S=Object.getOwnPropertyDescriptor;var N=Object.getOwnPropertyNames;var w=Object.getPrototypeOf,x=Object.prototype.hasOwnProperty;var C=(e,t)=>()=>(t||e((t={exports:{}}).exports,t),t.exports);var k=(e,t,a,n)=>{if(t&&typeof t=="object"||typeof t=="function")for(let o of N(t))!x.call(e,o)&&o!==a&&m(e,o,{get:()=>t[o],enumerable:!(n=S(t,o))||n.enumerable});return e};var A=(e,t,a)=>(a=e!=null?b(w(e)):{},k(t||!e||!e.__esModule?m(a,"default",{value:e,enumerable:!0}):a,e));var f=C((J,g)=>{g.exports=Vue});var B=Object.prototype.toString,p=e=>B.call(e),u=e=>p(e).slice(8,-1);function v(){return typeof __channelId__=="string"&&__channelId__}function E(e,t){switch(u(t)){case"Function":return"function() { [native code] }";default:return t}}function O(e,t,a){return v()?(a.push(t.replace("at ","uni-app:///")),console[e].apply(console,a)):a.map(function(o){let c=p(o).toLowerCase();if(["[object object]","[object array]","[object module]"].indexOf(c)!==-1)try{o="---BEGIN:JSON---"+JSON.stringify(o,E)+"---END:JSON---"}catch(s){o=c}else if(o===null)o="---NULL---";else if(o===void 0)o="---UNDEFINED---";else{let s=u(o).toUpperCase();s==="NUMBER"||s==="BOOLEAN"?o="---BEGIN:"+s+"---"+o+"---END:"+s+"---":o=String(o)}return o}).join("---COMMA---")+" "+t}function i(e,t,...a){let n=O(e,t,a);n&&console[e](n)}var r=A(f());var _=(e,t)=>{let a=e.__vccOpts||e;for(let[n,o]of t)a[n]=o;return a};var I={content:{"":{flex:1}},map:{"":{width:"750rpx",height:"500rpx",backgroundColor:"#000000"}},scrollview:{"":{flex:1}},button:{"":{marginTop:"30rpx",marginBottom:"20rpx"}}},h=weex.requireModule("mapSearch"),T={data(){return{markers:[{id:"1",latitude:39.908692,longitude:116.397477,title:"\u5929\u5B89\u95E8",zIndex:"1",iconPath:"/static/gps.png",width:20,height:20,anchor:{x:.5,y:1},callout:{content:`\u9996\u90FD\u5317\u4EAC
\u5929\u5B89\u95E8`,color:"#00BFFF",fontSize:12,borderRadius:2,borderWidth:0,borderColor:"#333300",bgColor:"#CCFF11",padding:"1",display:"ALWAYS"}}]}},methods:{selectPoint(e){i("log","at pages/API/map-search/map-search.nvue:46",e)},reverseGeocode(){var e=this.markers[0];h.reverseGeocode({point:{latitude:e.latitude,longitude:e.longitude}},t=>{i("log","at pages/API/map-search/map-search.nvue:56",JSON.stringify(t)),uni.showModal({content:JSON.stringify(t)})})},poiSearchNearBy(){var e=this.markers[0];h.poiSearchNearBy({point:{latitude:e.latitude,longitude:e.longitude},key:"\u505C\u8F66\u573A",radius:1e3},t=>{i("log","at pages/API/map-search/map-search.nvue:72",t),uni.showModal({content:JSON.stringify(t)})})}}};function P(e,t,a,n,o,c){let s=(0,r.resolveComponent)("button");return(0,r.openBlock)(),(0,r.createElementBlock)("scroll-view",{scrollY:!0,showScrollbar:!0,enableBackToTop:!0,bubble:"true",style:{flexDirection:"column"}},[(0,r.createElementVNode)("view",{class:"content"},[(0,r.createElementVNode)("map",{class:"map",ref:"dcmap",markers:o.markers,onTap:t[0]||(t[0]=(...y)=>c.selectPoint&&c.selectPoint(...y))},null,40,["markers"]),(0,r.createElementVNode)("scroll-view",{class:"scrollview",scrollY:"true"},[(0,r.createVNode)(s,{class:"button",onClick:c.reverseGeocode},{default:(0,r.withCtx)(()=>[(0,r.createTextVNode)("reverseGeocode")]),_:1},8,["onClick"]),(0,r.createVNode)(s,{class:"button",onClick:c.poiSearchNearBy},{default:(0,r.withCtx)(()=>[(0,r.createTextVNode)("poiSearchNearBy")]),_:1},8,["onClick"])])])])}var l=_(T,[["render",P],["styles",[I]]]);var d=plus.webview.currentWebview();if(d){let e=parseInt(d.id),t="pages/API/map-search/map-search",a={};try{a=JSON.parse(d.__query__)}catch(o){}l.mpType="page";let n=Vue.createPageApp(l,{$store:getApp({allowDefault:!0}).$store,__pageId:e,__pagePath:t,__pageQuery:a});n.provide("__globalStyles",Vue.useCssStyles([...__uniConfig.styles,...l.styles||[]])),n.mount("#root")}})();
