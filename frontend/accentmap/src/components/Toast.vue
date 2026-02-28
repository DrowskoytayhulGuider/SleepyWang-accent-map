<template>
  <div v-if="show" class="toast">
    {{ text }}
  </div>
</template>
<script setup>
import { ref } from 'vue'

const show = ref(false)
const text = ref('')

function showMessage(msg) {
  text.value = msg
  show.value = true
  setTimeout(() => show.value = false, 4000)
}
defineExpose({
  showMessage
})
</script>
<style scoped>
.toast {
  position: fixed;
  top: 30px;
  left: 50%;
  transform: translateX(-50%) translateY(0);
  background: #323232;
  opacity: 0;
  color: white;
  padding: 10px 20px;
  border-radius: 6px;
  z-index: 1000;
  animation: fadeUp 0.7s ease forwards;
}
@keyframes fadeUp {
  to {
    opacity: 0.8;
    transform:translateX(-50%) translateY(10px);
  }
}
</style>
