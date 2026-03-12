<template>
  <toast ref="toastRef"/>
  <div id="view-container">
    <div class="content">
      <div id="search-container">
        <input type="text" placeholder="搜索关键词" class="textbox" v-model="searchSubdialect" style="width: 200px">
        <button @click="search" id="searchSubdialect">搜索🔍</button>
      </div>
      <div id="add-container" >
        <input type="text" placeholder="输入要添加的方言小片名称" class="textbox" v-model="newSName" style="width: 100px">
        <DroppableInput
            v-model="selectedDialect"
            :filteredList="filteredDialects"
            :clickHandler="onLClick"
            :itemClick="liOnClick"
            placeholder="输入其所属方言片"
            :field="'dname'"
            :input-style="'width:100px'"
        />

        <button @click="add" id="addDialect">添加➕</button>
      </div>
      <div class="title">当前数据库中方言小片如下（共{{subdialects.length}}个）：</div>
      <div id="properties">
        <span>方言小片</span><span>方言片</span><span style="width: 100px">操作</span>
      </div>
      <ul v-if="selectedSubdialects.length" ref="listRef">
        <li v-for="subdialect in selectedSubdialects" :key="selectedSubdialects.sid">
          <input type="text" v-model="subdialect.sname" class="textbox" style="width: 100px">
          <DroppableInput
              v-model="listSelectedDialects[subdialect.sid]"
              :filteredList="filterListDialects(subdialect.sid)"
              :clickHandler="() => onListLClick(subdialect)"
              :itemClick="(dialect) => listLiOnClick(dialect, subdialect)"
              :field="'dname'"
              :input-style="'width:100px'"
          />
          <div>
            <a @click="change(subdialect)">修改</a><a @click="deleteSubdialect(subdialect)">删除</a><br><router-link :to="'/accents/'+subdialect.sid">查看下属口音</router-link>
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