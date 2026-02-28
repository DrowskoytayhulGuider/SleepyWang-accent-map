<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchDialect">
        <button @click="search" id="searchDialect">搜索🔍</button>
      </div>
      <div id="add-container" >
        <input type="text" placeholder="输入要添加的方言片名称" class="textbox" v-model="newDName">
        <DroppableInput
            v-model="selectedLanguage"
            :filteredList="filteredLanguages"
            :clickHandler="onLClick"
            :itemClick="liOnClick"
            placeholder="输入其所属大方言区"
            :inputStyle="'padding-top: 10px;padding-bottom: 10px;width: 200px;text-align: left;margin-right: 10px'"
            :field="'lname'"
        />

        <button @click="add" id="addLanguage">添加➕</button>
      </div>
      <div class="title">当前数据库中方言片如下（共{{dialects.length}}个）：</div>
      <div id="properties">
        <span>方言片</span><span>大方言区</span><span style="width: 220px">操作</span>
      </div>
      <ul v-if="selectedDialects.length" ref="listRef">
        <li v-for="dialect in selectedDialects" :key="selectedDialects.did">
          <input type="text" v-model="dialect.dname" class="textbox">
          <DroppableInput
              v-model="listSelectedLanguages[dialect.did]"
              :filteredList="filterListLanguages(dialect.did)"
              :clickHandler="() => onListLClick(dialect)"
              :itemClick="(language) => listLiOnClick(language, dialect)"
              :inputStyle="'width:120px;display:inline-block;font-size:16px;padding:2px;text-align:center;margin-right:10px;'"
              :field="'lname'"
          />
          <a @click="change(dialect)">修改</a><a @click="deleteDialect(dialect)">删除</a><router-link :to="'/subdialects/'+dialect.did">查看下属方言小片</router-link>
        </li>
      </ul><br>
    </div>
  </div>
</template>

<script setup>
import {computed, nextTick, onMounted, ref} from "vue";
import request from "@/utils/request.js";
import DroppableInput from "@/components/DroppableInput.vue";
import Toast from "@/components/Toast.vue";

const props = defineProps({
  id: String
})
let toastRef=ref(null);
let dialects=ref([])
let newDName=ref('')
let listRef=ref(null)
let searchDialect=ref('')
let selectedDialects=ref([])
let languages=ref([])
let listSelectedLanguages=ref({})
let listLanguages=ref({})
let selectedLanguage=ref({lname:null,lid:24});
const filteredLanguages=computed(() => {
  // return languages.value
  if (!selectedLanguage.value.lname) return languages.value
  return languages.value.filter(o =>
      o.lname.includes(selectedLanguage.value.lname)
  )
})
const filterListLanguages=(did)=>{
  if (!listSelectedLanguages.value[did].lname) return listLanguages.value[did]
  return listLanguages.value[did].filter(o =>
      o.lname.includes(listSelectedLanguages.value[did].lname)
  )
}
async function onLClick()
{
  if (!props.id) {
    languages.value = (await request.get("/accent/languages")).data
  } else {
    let language = (await request.get("/accent/language/"+props.id)).data
    languages.value.push(language)
    selectedDialects.value = dialects.value
  }
  languages.value = languages.value.filter(o =>
      o.lname !== '暂无数据'
  )

}
async function onListLClick(dialect)
{
  if (!props.id) {
    listLanguages.value[dialect.did] = (await request.get("/accent/languages")).data
  } else {
    let language = (await request.get("/accent/language/"+props.id)).data
    listLanguages.value[dialect.did]=[language]
    // selectedDialects.value = dialects.value
  }
  listLanguages.value[dialect.did] = listLanguages.value[dialect.did].filter(o =>
      o.lname !== '暂无数据'
  )
}
function liOnClick(language)
{
  selectedLanguage.value=language;
}
function listLiOnClick(language,dialect)
{
  listSelectedLanguages.value[dialect.did]=language;
  dialect.language=language
  dialect.lid=language.lid
}
async function search()
{
  if(document.getElementById("searchDialect").textContent==='搜索🔍') {
    selectedDialects.value=dialects.value.filter(o =>
        o.dname.includes(searchDialect.value)
    )
    document.getElementById("searchDialect").textContent = '清空❎'
  }
  else {
    searchDialect.value=null;
    selectedDialects.value=dialects.value
    document.getElementById("searchDialect").textContent='搜索🔍'
  }

}
async function change(dialect)
{
  console.log(dialect)
  if(!dialect.lid||!dialect.dname)
  {
    toastRef.value.showMessage("❎方言片名称和所属大方言区不能为空！");
    return;
  }
  await request.put("/accent/dialect",dialect)
}
async function add()
{
  if(!newDName.value||!selectedLanguage.value.lid||selectedLanguage.value.lid===24)
  {
    toastRef.value.showMessage("❎方言片名称和所属大方言区不能为空！");
    return;
  }
  const did=(await request.post("/accent/dialect", {
    dname: newDName.value,
    lid:selectedLanguage.value.lid
  })).data

  dialects.value.push({
    did:did,
    dname:newDName.value,
    lname:selectedLanguage.value.lname,
    lid:selectedLanguage.value.lid
  })
  listLanguages.value[did]=[];
  // console.log(listSubdialects.value[accent.aid])
  listSelectedLanguages.value[did]=selectedLanguage.value
  newDName.value=''
  await nextTick()
  listRef.value.scrollTop = listRef.value.scrollHeight
}
async function deleteDialect(dialect)
{
  if(confirm("⚠️确定删除吗？删除该方言片其下属的方言小片会一并删除，您必须知道这是不可逆的！")) {
    await request.delete(`/accent/dialect/${dialect.did}`)
    dialects.value.splice(dialects.value.indexOf(dialect), 1)
  }
}
onMounted(async ()=>{
  if(!props.id) {
    dialects.value = (await request.get("/accent/dialects")).data
    dialects.value = dialects.value.filter(o =>
        o.dname !== '暂无数据'
    )
    selectedDialects.value = dialects.value

  }
  else
  {
    dialects.value = (await request.get(`/accent/dialects/${props.id}`)).data
    dialects.value = dialects.value.filter(o =>
        o.dname !== '暂无数据'
    )
    selectedDialects.value = dialects.value
  }
  for(let dialect of dialects.value)
  {
    listLanguages.value[dialect.did]=[];
    console.log(listLanguages.value[dialect.did])
    listSelectedLanguages.value[dialect.did]=dialect.language
  }
  document.onclick=()=>{
    languages.value=[]
    for(let key in listLanguages.value)
    {
      listLanguages.value[key]=[]
    }
  }
})

</script>

<style scoped>
#properties{
  /*background-color: white;*/
  display: inline-block;
  width: 60%;
  text-align: left;
  padding-left: 30px;
}
span{
  text-align: center;
  display: inline-block;
  width: 120px;
  margin-right: 10px;
  background-color: rgba(155,220,255,0.8);
}

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
  width: 120px;
  display: inline-block;
  /*background-color: #4a90e2;*/
  font-size: 16px;
  padding: 2px;
  text-align: center;
  margin-right: 10px;
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
#search-container .textbox{
  width: 410px;
}
div > .textbox{
  padding-top: 10px;
  padding-bottom: 10px;
  width: 200px;
  text-align: left;
}
.dropdown{
  width: auto;
}
.dropdown-content{
  max-height: 200px;
  width: 150px;
}
</style>