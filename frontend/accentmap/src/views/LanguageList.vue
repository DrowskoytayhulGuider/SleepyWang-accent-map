<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchLanguage">
        <button @click="search" id="searchLanguage">搜索🔍</button>
      </div>
      <div id="add-container">
        <input type="text" placeholder="输入要添加的大方言区名称" class="textbox" v-model="newLName">
        <button @click="add" id="addLanguage">添加➕</button>
      </div>
      <div class="title">当前数据库中大方言区如下（共{{languages.length}}个）：</div>

      <ul v-if="selectedLanguages.length" ref="listRef">
        <li v-for="language in selectedLanguages" :key="language.lid">
          <input type="text" v-model="language.lname" class="textbox">
          <a @click="change(language)">修改</a><a @click="deleteLanguage(language)">删除</a><router-link :to="'/dialects/'+language.lid">查看下属方言片</router-link>
        </li>
      </ul><br>


    </div>
  </div>

</template>

<script setup>
import { nextTick, onMounted, ref} from "vue";
import request from "@/utils/request";
import Toast from "@/components/Toast.vue";

let toastRef=ref(null)
let languages=ref([])
let newLName=ref('')
let listRef=ref(null)
let searchLanguage=ref('')
let selectedLanguages=ref([])
async function search()
{
  if(document.getElementById("searchLanguage").textContent==='搜索🔍') {
    selectedLanguages.value=languages.value.filter(o =>
        o.lname.includes(searchLanguage.value)
    )
    document.getElementById("searchLanguage").textContent = '清空❎'
  }
  else {
    searchLanguage.value=null;
    selectedLanguages.value=languages.value
    document.getElementById("searchLanguage").textContent='搜索🔍'
  }

}
async function change(language)
{
  if(!language.lname)
  {
    toastRef.value.showMessage("❎大方言区名称不能为空！");
    return;
  }
  await request.put("/accent/language",language)
}
async function add()
{
  if(!newLName.value)
  {
    toastRef.value.showMessage("❎大方言区名称不能为空！");
    return;
  }
  const lid=(await request.post("/accent/language", {
    lname: newLName.value
  })).data
  console.log("lid:"+lid)
  languages.value.push({
    lid:lid,
    lname:newLName.value
  })
  newLName.value=''
  await nextTick()
  listRef.value.scrollTop = listRef.value.scrollHeight
}
async function deleteLanguage(language)
{
  if(confirm("⚠️确定删除吗？删除该大方言区其下属的方言片会一并删除，您必须知道这是不可逆的！")) {
    await request.delete(`/accent/language/${language.lid}`)
    languages.value.splice(languages.value.indexOf(language), 1)
  }
}
onMounted(async ()=>{
  languages.value=(await request.get("/accent/languages")).data
  languages.value=languages.value.filter(o =>
      o.lname!=='暂无数据'
  )
  selectedLanguages.value=languages.value
})
</script>

<style scoped>
#view-container{
  width: 100vw;
  height: 100vh;
  background-color: rgba(255,241,241,0.8);
  text-align: center;
}
a,:deep(a)
{
  margin-left: 20px;
  font-size: 12px;
}
a:hover{
  cursor: pointer;
}
.title{
  margin-top: 10px;
  font-size: 20px;
  font-weight: bold;
}
ul{
  /*background-color: white;*/
  margin-top: 10px;
  display: inline-block;
  width: 60%;
  max-height: 500px;
  overflow-y: auto;    /* 超出显示纵向滚动条 */
  overflow-x: hidden;  /* 防止横向滚动条 */;
}
li{
  margin-top: 10px;
}
.textbox{
  width: 90px;
  display: inline-block;
  /*background-color: #4a90e2;*/
  font-size: 16px;
  padding: 2px;
  text-align: center;
}
.content{
  display: inline-block;
  width: 60%;
  height: 100%;
  background-color: rgba(241,250,225,80%);

}
button{
  /*position: absolute;*/
  /*top: 90%;*/
  /*left: 47%;*/
  /*background-color: rgb(76, 175, 80)*/
  margin-left: 10px;
}
#add-container{
  margin-top: 10px;
}
#search-container
{
  margin-top: 10px;
}
div > .textbox{
  padding-top: 10px;
  padding-bottom: 10px;
  width: 250px;
  text-align: left;
}
</style>