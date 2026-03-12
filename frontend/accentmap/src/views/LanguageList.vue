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
          <input type="text" v-model="language.lname" class="textbox" style="width: 100px">
          <div>
            <a @click="change(language)">修改</a><a @click="deleteLanguage(language)">删除</a><router-link :to="'/dialects/'+language.lid">查看下属方言片</router-link>
          </div>
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
/* ---------- 全局容器 ---------- */
#view-container{
  width: 100vw;
  min-height: 100vh;
  /*background-color: rgba(255,241,241,0.8);*/
  background-color: #4a90e2;
  text-align: center;
}

.content{
  /*display: flex;*/
  /*flex-direction: column;*/
  /*align-items: center;*/
  text-align: left;
  /*width: 100%;*/
  height: 100%;
  max-width: 600px;
  padding-left: 8%;
  margin: 0 auto;
  /*padding: 1rem;*/
  background-color: rgba(241,250,225,0.8);
  border-radius: 0.5rem;
}

/* ---------- 链接 ---------- */
a, :deep(a){
  margin-left: 0.5rem;
  font-size: 0.8rem;
}
a:hover{
  cursor: pointer;
}

/* ---------- 标题 ---------- */
.title{
  margin-top: 1rem;
  font-size: 1.2rem;
  font-weight: bold;
}

/* ---------- 表头 ---------- */
#properties{
  display: flex;
  justify-content: flex-start;
  width: 100%;
  max-width: 700px;
  margin-top: 1rem;
  padding-left: 0.5rem;
}

#properties span{
  /*flex: 2;*/
  text-align: center;
  display: inline-block;
  width: 100px;
  margin-right: 10px;
  background-color: rgba(155,220,255,0.8);
  padding: 0.3rem;
  border-radius: 0.3rem;
}

/* ---------- 搜索/添加 ---------- */
#search-container,
#add-container{
  width: 100%;
  text-align: left;
}
#add-container .textbox,
#search-container .textbox{
  width: 200px;
}


/* ---------- 列表 ---------- */
ul{
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
  max-width: 700px;
  height: 100%;
  padding: 0;
  list-style: none;
  overflow-y: auto;
  max-height: 500px;
}

li{
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
}
li input{
  margin-left: 5px;
  margin-right: 5px;
}
/*li input,li div{*/
/*  flex: 1;*/
/*}*/

/*li input{*/
/*  width: 220px;*/
/*}*/
/* ---------- 按钮 ---------- */
button{
  padding: 0.3rem 0.6rem;
  font-size: 1rem;
  border-radius: 0.3rem;
  border: none;
  background-color: #4a90e2;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  min-width: 80px;
}
button:hover{
  background-color: #357bd8;
}
button:active{
  transform: scale(0.96);
}


</style>