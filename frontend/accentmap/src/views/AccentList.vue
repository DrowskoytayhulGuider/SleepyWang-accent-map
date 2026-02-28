<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchAccent">
        <button @click="search" id="searchAccent">搜索🔍</button>
      </div>
      <div id="add-container" >
        <input type="text" placeholder="输入要添加的口音名称" class="textbox" v-model="newAName">
        <DroppableInput
            v-model="selectedSubdialect"
            :filteredList="filteredSubdialects"
            :clickHandler="onLClick"
            :itemClick="liOnClick"
            placeholder="输入其所属方言小片"
            :inputStyle="'padding-top: 10px;padding-bottom: 10px;width: 200px;text-align: left;margin-right: 10px'"
            :field="'sname'"
        />

        <button @click="add" id="addSubdialect">添加➕</button>
      </div>
      <div class="title">当前数据库中口音如下（共{{accents.length}}个）：</div>
      <div id="properties">
        <span>口音</span><span>方言小片</span><span style="width: 180px">操作</span>
      </div>
      <ul v-if="selectedAccents.length" ref="listRef">
        <li v-for="accent in selectedAccents" :key="selectedAccents.aid">
          <input type="text" v-model="accent.aname" class="textbox">
          <DroppableInput
              v-model="listSelectedSubdialects[accent.aid]"
              :filteredList="filterListSubdialects(accent.aid)"
              :clickHandler="() => onListLClick(accent)"
              :itemClick="(subdialect) => listLiOnClick(subdialect, accent)"
              :inputStyle="'width:120px;display:inline-block;font-size:16px;padding:2px;text-align:center;margin-right:10px;'"
              :field="'sname'"
          />
          <a @click="change(accent)">修改</a><a @click="deleteAccent(accent)">删除</a>
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
let accents=ref([])
let newAName=ref('')
let listRef=ref(null)
let searchAccent=ref('')
let selectedAccents=ref([])
let subdialects=ref([])
let listSelectedSubdialects=ref({})
let listSubdialects=ref({})
let selectedSubdialect=ref({sname:null,sid:34});
let toastRef=ref(null);
const filteredSubdialects=computed(() => {
  // return subdialects.value
  if (!selectedSubdialect.value.sname) return subdialects.value
  return subdialects.value.filter(o =>
      o.sname.includes(selectedSubdialect.value.sname)
  )
})
const filterListSubdialects=(aid)=>{
  if (!listSelectedSubdialects.value[aid].sname) return listSubdialects.value[aid]
  return listSubdialects.value[aid].filter(o =>
      o.sname.includes(listSelectedSubdialects.value[aid].sname)
  )
}
async function onLClick()
{
  if (!props.id) {
    subdialects.value = (await request.get("/accent/subdialects")).data
  } else {
    let subdialect = (await request.get("/accent/subdialect/"+props.id)).data
    subdialects.value.push(subdialect)
    selectedAccents.value = accents.value
  }
  subdialects.value = subdialects.value.filter(o =>
      o.sname !== '暂无数据'
  )

}
async function onListLClick(accent)
{
  if (!props.id) {
    listSubdialects.value[accent.aid] = (await request.get("/accent/subdialects")).data
  } else {
    let subdialect = (await request.get("/accent/subdialect/"+props.id)).data
    listSubdialects.value[accent.aid]=[subdialect]
    // selectedAccents.value = accents.value
  }
  listSubdialects.value[accent.aid] = listSubdialects.value[accent.aid].filter(o =>
      o.sname !== '暂无数据'
  )
}
function liOnClick(subdialect)
{
  selectedSubdialect.value=subdialect;
}
function listLiOnClick(subdialect,accent)
{
  listSelectedSubdialects.value[accent.aid]=subdialect;
  accent.sid=subdialect.sid
  accent.subdialect=subdialect
}
async function deleteAccent(accent) {
  console.log(accent.aid)
  await request.delete(`/accent/accent/${accent.aid}`)
  accents.value.splice(accents.value.indexOf(accent), 1)
}
async function search()
{
  if(document.getElementById("searchAccent").textContent==='搜索🔍') {
    selectedAccents.value=accents.value.filter(o =>
        o.aname.includes(searchAccent.value)
    )
    document.getElementById("searchAccent").textContent = '清空❎'
  }
  else {
    searchAccent.value=null;
    selectedAccents.value=accents.value
    document.getElementById("searchAccent").textContent='搜索🔍'
  }

}
async function change(accent)
{
  if(!accent.sid||!accent.aname)
  {
    toastRef.value.showMessage("❎口音名称和所属方言小片不能为空！");
    return;
  }
  await request.put("/accent/accent",accent)
}
async function add()
{
  if(!newAName.value||!selectedSubdialect.value.sid||selectedSubdialect.value.sid===34)
  {
    toastRef.value.showMessage("❎口音名称和所属方言小片不能为空！");
    return;
  }
  const aid=(await request.post("/accent/accent", {
    aname: newAName.value,
    sid:selectedSubdialect.value.sid
  })).data
  console.log("selectedSubdialect")
  console.log(selectedSubdialect.value)
  accents.value.push({
    aid:aid,
    aname:newAName.value,
    sname:selectedSubdialect.value.sname,
    sid:selectedSubdialect.value.sid
  })
  listSubdialects.value[aid]=[];
  // console.log(listSubdialects.value[accent.aid])
  listSelectedSubdialects.value[aid]=selectedSubdialect.value
  newAName.value=''
  await nextTick()
  listRef.value.scrollTop = listRef.value.scrollHeight
}
// async function deletenewAName(accent)
// {
//   console.log(accent.sid)
//   await request.delete(`/accent/subdialect/${accent.sid}`)
//   accents.value.splice(accents.value.indexOf(accent),1)
// }
onMounted(async ()=>{
  if(!props.id) {
    accents.value = (await request.get("/accent/accents")).data
    accents.value = accents.value.filter(o =>
        o.aname !== '暂无数据'
    )
    selectedAccents.value = accents.value
  }
  else
  {
    accents.value = (await request.get(`/accent/accents/${props.id}`)).data
    accents.value = accents.value.filter(o =>
        o.aname !== '暂无数据'
    )
    selectedAccents.value = accents.value

  }
  for(let accent of accents.value)
  {
    // console.log(accents.value.length)
    listSubdialects.value[accent.aid]=[];
    // console.log(listSubdialects.value[accent.aid])
    listSelectedSubdialects.value[accent.aid]=accent.subDialect
  }
  document.onclick=()=>{
    subdialects.value=[]
    for(let key in listSubdialects.value)
    {
      listSubdialects.value[key]=[]
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
  /*padding-left: 87px;*/
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
  text-align: left;
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