<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchSubdialect">
        <button @click="search" id="searchSubdialect">搜索🔍</button>
      </div>
      <div id="add-container" >
        <input type="text" placeholder="输入要添加的方言小片名称" class="textbox" v-model="newSName">
        <DroppableInput
            v-model="selectedDialect"
            :filteredList="filteredDialects"
            :clickHandler="onLClick"
            :itemClick="liOnClick"
            placeholder="输入其所属方言片"
            :inputStyle="'padding-top: 10px;padding-bottom: 10px;width: 200px;text-align: left;margin-right: 10px'"
            :field="'dname'"
        />

        <button @click="add" id="addDialect">添加➕</button>
      </div>
      <div class="title">当前数据库中方言小片如下（共{{subdialects.length}}个）：</div>
      <div id="properties">
        <span>方言小片</span><span>方言片</span><span style="width: 220px">操作</span>
      </div>
      <ul v-if="selectedSubdialects.length" ref="listRef">
        <li v-for="subdialect in selectedSubdialects" :key="selectedSubdialects.sid">
          <input type="text" v-model="subdialect.sname" class="textbox">
          <DroppableInput
              v-model="listSelectedDialects[subdialect.sid]"
              :filteredList="filterListDialects(subdialect.sid)"
              :clickHandler="() => onListLClick(subdialect)"
              :itemClick="(dialect) => listLiOnClick(dialect, subdialect)"
              :inputStyle="'width:120px;display:inline-block;font-size:16px;padding:2px;text-align:center;margin-right:10px;'"
              :field="'dname'"
          />
          <a @click="change(subdialect)">修改</a><a @click="deleteSubdialect(subdialect)">删除</a><router-link :to="'/accents/'+subdialect.sid">查看下属口音</router-link>
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
let subdialects=ref([])
let newSName=ref('')
let listRef=ref(null)
let searchSubdialect=ref('')
let selectedSubdialects=ref([])
let dialects=ref([])
let listSelectedDialects=ref({})
let listDialects=ref({})
let selectedDialect=ref({dname:null,did:35});
const filteredDialects=computed(() => {
  // return dialects.value
  if (!selectedDialect.value.dname) return dialects.value
  return dialects.value.filter(o =>
      o.dname.includes(selectedDialect.value.dname)
  )
})
const filterListDialects=(sid)=>{
  if (!listSelectedDialects.value[sid].dname) return listDialects.value[sid]
  return listDialects.value[sid].filter(o =>
      o.dname.includes(listSelectedDialects.value[sid].dname)
  )
}
async function onLClick()
{
  if (!props.id) {
    dialects.value = (await request.get("/accent/dialects")).data
  } else {
    let dialect = (await request.get("/accent/dialect/"+props.id)).data
    dialects.value.push(dialect)
    selectedSubdialects.value = subdialects.value
  }
  dialects.value = dialects.value.filter(o =>
      o.dname !== '暂无数据'
  )

}
async function onListLClick(subdialect)
{
  if (!props.id) {
    listDialects.value[subdialect.sid] = (await request.get("/accent/dialects")).data
  } else {
    let dialect = (await request.get("/accent/dialect/"+props.id)).data
    listDialects.value[subdialect.sid]=[dialect]
    // selectedSubdialects.value = subdialects.value
  }
  listDialects.value[subdialect.sid] = listDialects.value[subdialect.sid].filter(o =>
      o.dname !== '暂无数据'
  )
}
function liOnClick(dialect)
{
  selectedDialect.value=dialect;
}
function listLiOnClick(dialect,subdialect)
{
  listSelectedDialects.value[subdialect.sid]=dialect;
  subdialect.dialect=dialect
  subdialect.did=dialect.did
}
async function search()
{
  if(document.getElementById("searchSubdialect").textContent==='搜索🔍') {
    selectedSubdialects.value=subdialects.value.filter(o =>
        o.sname.includes(searchSubdialect.value)
    )
    document.getElementById("searchSubdialect").textContent = '清空❎'
  }
  else {
    searchSubdialect.value=null;
    selectedSubdialects.value=subdialects.value
    document.getElementById("searchSubdialect").textContent='搜索🔍'
  }

}
async function change(subdialect)
{
  if(!subdialect.did||!subdialect.sname)
  {
    toastRef.value.showMessage("❎方言小片名称和所属方言片不能为空！");
    return;
  }
  await request.put("/accent/subdialect",subdialect)
}
async function add()
{
  if(!newSName.value||!selectedDialect.value.did||selectedDialect.value.did===35)
  {
    toastRef.value.showMessage("❎方言小片名称和所属方言片不能为空！");
    return;
  }
  const sid=(await request.post("/accent/subdialect", {
    sname: newSName.value,
    did:selectedDialect.value.did
  })).data

  subdialects.value.push({
    sid:sid,
    sname:newSName.value,
    dname:selectedDialect.value.dname,
    did:selectedDialect.value.did
  })
  listDialects.value[sid]=[];
  // console.log(listSubdialects.value[accent.aid])
  listSelectedDialects.value[sid]=selectedDialect.value
  newSName.value=''
  await nextTick()
  listRef.value.scrollTop = listRef.value.scrollHeight
}
async function deleteSubdialect(subdialect)
{
  if(confirm("⚠️确定删除吗？删除该方言小片其下属的口音会一并删除，您必须知道这是不可逆的！")) {
    await request.delete(`/accent/subdialect/${subdialect.sid}`)
    subdialects.value.splice(subdialects.value.indexOf(subdialect), 1)
  }
}
onMounted(async ()=>{
  if(!props.id) {
    subdialects.value = (await request.get("/accent/subdialects")).data
    subdialects.value = subdialects.value.filter(o =>
        o.sname !== '暂无数据'
    )
    selectedSubdialects.value = subdialects.value

  }
  else
  {
    subdialects.value = (await request.get(`/accent/subdialects/${props.id}`)).data
    subdialects.value = subdialects.value.filter(o =>
        o.sname !== '暂无数据'
    )
    selectedSubdialects.value = subdialects.value
  }
  for(let subdialect of subdialects.value)
  {
    listDialects.value[subdialect.sid]=[];
    console.log(listDialects.value[subdialect.sid])
    listSelectedDialects.value[subdialect.sid]=subdialect.dialect
  }
  document.onclick=()=>{
    dialects.value=[]
    for(let key in listDialects.value)
    {
      listDialects.value[key]=[]
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