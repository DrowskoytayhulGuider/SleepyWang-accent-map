<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchDialect" style="width: 200px">
        <button @click="search" >搜索🔍</button>
      </div>
      <div id="add-container" >
        <input type="text" placeholder="输入要添加的方言片名称" class="textbox" v-model="newDName" style="width: 100px">
        <DroppableInput
            v-model="selectedLanguage"
            :filteredList="filteredLanguages"
            :clickHandler="onLClick"
            :itemClick="liOnClick"
            placeholder="输入其所属大方言区"
            :field="'lname'"
            :input-style="'width:100px'"
        />
<!--        :inputStyle="'padding-top: 10px;padding-bottom: 10px;width: 25vw;text-align: left;margin-right: 10px'"-->
        <button @click="add" id="addLanguage">添加➕</button>
      </div>
      <div class="title">当前数据库中方言片如下（共{{dialects.length}}个）：</div>
      <div id="properties">
        <span>方言片</span><span>大方言区</span><span style="width: 100px">操作</span>
      </div>
      <ul v-if="selectedDialects.length" ref="listRef">
        <li v-for="dialect in selectedDialects" :key="selectedDialects.did">
          <input type="text" v-model="dialect.dname" class="textbox" style="width: 100px">
          <DroppableInput
              v-model="listSelectedLanguages[dialect.did]"
              :filteredList="filterListLanguages(dialect.did)"
              :clickHandler="() => onListLClick(dialect)"
              :itemClick="(language) => listLiOnClick(language, dialect)"
              :field="'lname'"
              :input-style="'width:100px'"
          />
<!--          :inputStyle="'width: 12vw;display:inline-block;font-size:16px;padding:2px;text-align:center;margin-right:10%;'"-->
          <div>
            <a @click="change(dialect)">修改</a><a @click="deleteDialect(dialect)">删除</a><br><router-link :to="'/subdialects/'+dialect.did">查看下属小片</router-link>
          </div>
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
#search-container .textbox{
  width: 25%;
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

/* ---------- 下拉 ---------- */
.dropdown{
  width: auto;
}

.dropdown-content{
  max-height: 200px;
  width: 150px;
  overflow-y: auto;
}


</style>