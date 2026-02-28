<template>
  <Toolbox v-if="map" :map="map"/>
  <div id="container"></div>


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

  /*Toolbox{*/
  /*  width: 1000px;*/
  /*  background-color: #4a90e2;*/
  /*}*/
</style>