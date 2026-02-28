<template>
  <div class="dropdown">
    <input
        type="text"
        :placeholder="placeholder"
        class="textbox"
        :style="inputStyle"
        :value="modelValue[field]"
        @input="handleInput"
        @click="clickHandler"
    />

    <ul v-if="filteredList.length" class="dropdown-content">
      <li
          v-for="language in filteredList"
          :key="language.lid"
          @click="itemClick(language)"
      >
        {{ language[field] }}
      </li>
    </ul>
  </div>
</template>

<script setup>


const props = defineProps({
  modelValue: Object,          // v-model 绑定对象（必须保持）
  filteredList: Array,        // 下拉列表数据
  clickHandler: Function,     // 点击 input 时执行的方法
  itemClick: Function,        // 点击 li 时执行的方法
  placeholder: String,        // 输入框提示
  inputStyle: String,          // 输入框样式（用于第二个 dropdown）
  field:String
})

const emit=defineEmits(['update:modelValue','input'])
function handleInput(e)
{
  props.modelValue[props.field] = e.target.value
  emit('input', e)
}

</script>