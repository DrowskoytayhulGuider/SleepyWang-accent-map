<template>
  <div id="tool-container">
    <Toolbox v-if="map" :map="map"/>
    <div id="container"></div>
  </div>


</template>

<script setup>
import {onMounted, onUnmounted, ref} from "vue";
// import {loadAMap} from "@/services/amapLoader.js";
import Toolbox from "@/components/Toolbox.vue";


const map = ref(null)
// const AMap = ref(null)

onMounted(async () => {
  // AMap.value = await loadAMap()

  map.value = new AMap.Map("container", {
    zoom: 11,
    keyboardEnable: false
  })
})

onUnmounted(() => {
  map.value?.destroy()
})

</script>

<style  scoped>

  /*#tool-container{*/
  /*  width: 20vw;*/
  /*  z-index: 10;*/
  /*  !*display: inline-block;*!*/
  /*}*/
</style>