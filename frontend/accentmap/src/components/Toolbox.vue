<template>
  <toast ref="toastRef"/>
  <div id="selector" @click="menuOnClick">
    <MenuOutlined style="color: white"/>
  </div>
  <div id="toolbox">
    <transition name="menu">
    <div id="dialectSelector" v-show="isShowingMenu" @click="closeDropdown" :class="{open:open}">
      <label>大方言区：</label>
      <DroppableInput
          v-model="selectedLanguage"
          :filtered-list="filteredLanguages"
          :click-handler="languageOnClick"
          :item-click="languageLiOnClick"
          @input="onLInput"
          :placeholder="'大方言区'"
          :field="'lname'"
          :input-style="'margin-bottom: 5%;margin-top: 10%'"
      />
      <br>
      <label>方言片：</label>
      <DroppableInput
          v-model="selectedDialect"
          :filtered-list="filteredDialects"
          :click-handler="dialectOnClick"
          :item-click="dialectLiOnClick"
          @input="onDInput"
          :placeholder="'方言片'"
          :field="'dname'"
          :input-style="'margin-bottom: 5%'"
      />
      <br>
      <label>方言小片：</label>
      <DroppableInput
          v-model="selectedSubdialect"
          :filtered-list="filteredSubdialects"
          :click-handler="subDialectOnClick"
          :item-click="subDialectLiOnClick"
          @input="onSInput"
          :placeholder="'方言小片'"
          :field="'sname'"
          :input-style="'margin-bottom: 5%'"
      />
      <br>
      <label>口音：</label>
      <DroppableInput
          v-model="selectedAccent"
          :filtered-list="filteredAccents"
          :click-handler="accentOnClick"
          :item-click="accentLiOnClick"
          @input="onAInput"
          :placeholder="'口音'"
          :field="'aname'"
          :input-style="'margin-bottom: 5%'"
      />
      <br>
      <router-link to="/backend" id="enter-backend">进入后台...</router-link><br>
      <div style="text-align: center">
        <button @click="findCountiesByDialects">筛选⏳</button>
        <button @click="clearPolygon(true)">清空❌</button>
      </div>


    </div>
    </transition>
    <div id="searchBox">
      <button id="mark" @click="markOnClick">定点📌</button>
      <button id="ruler" @click="rulerOnClick">测距📏</button>
      <button id="select" @click="selectOnClick">框选✍️</button>
      <input type="text" class="textbox" id="searchtext" ref="searchInput">
      <button id="searchbutton" @click="searchKeyword">搜索🔍</button>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from "vue"
import request from "@/utils/request.js";
import DroppableInput from "@/components/DroppableInput.vue";
import pako from 'pako';
import Toast from "@/components/Toast.vue";
import { MenuOutlined } from '@ant-design/icons-vue'
const props = defineProps({
  map: Object,
})
let searchInput=ref(null);
let district;
let polygons = []
let markers = []
let geocoder=null;
let isDrawingSegment=false;
let pointHeads=[];
let selectedArea,selectedAdcodes=[];
let segmentHeads=[];
let textHeads=[];
let languages=ref([]);
let dialects=ref([]);
let subdialects=ref([]);
let accents=ref([]);
let selectedLanguage=ref({lname:null,lid:24});
let selectedDialect=ref({dname:null,did:35});
let selectedSubdialect=ref({sname:null,sid:34});
let selectedAccent=ref({aname:null,aid:260});
let toastRef=ref(null)
let isShowingMenu=ref(false);
let isMarkOn = 0
let isSelectOn = 0
let isRulerOn = 0
const filteredLanguages = computed(() => {
  if (!selectedLanguage.value.lname) return languages.value
  return languages.value.filter(o =>
      o.lname.includes(selectedLanguage.value.lname)
  )
})
const filteredDialects = computed(() => {
  if (!selectedDialect.value.dname) return dialects.value
  return dialects.value.filter(o =>
      o.dname.includes(selectedDialect.value.dname)
  )
})
const filteredSubdialects = computed(() => {
  if (!selectedSubdialect.value.sname) return subdialects.value
  return subdialects.value.filter(o =>
      o.sname.includes(selectedSubdialect.value.sname)
  )
})
const filteredAccents = computed(() => {
  if (!selectedAccent.value.aname) return accents.value
  return accents.value.filter(o =>
      o.aname.includes(selectedAccent.value.aname)
  )
})
const infoWindowContent=[
  "<div class='info'>",
  "<div id='infotext' style='text-align: center;font-size:15px '>",
  "</div><hr>",
  "<div id='position' >坐标：</div>",
  "<div id='markerDialect'>方言：（请用菜单里的方言筛选框输入）",
  "<input type='text' id='language-info' class='info-text' disabled>",
  "<input type='text' id='dialect-info' class='info-text' disabled>",
  "<input type='text' id='subdialect-info' class='info-text' disabled>",
  "<input type='text' id='accent-info' class='info-text' disabled>",
  "<a id='clear-dialect-info'>清空❌</a>",
  "</div>",
  "<div id='remarkbox'>备注：",
  "<textarea id='remark' class='textbox' style='width: 280px;'></textarea>",
  "</div>",
  "<div id='buttoncontainer'>",
  "<button id='deleteMarker'>删除🗑️</button>",
  "<button id='changeInfo'>保存💾️</button>",
  "<button id='closeWindow'>关闭❎</button>",
  "</div>",
  "</div>",];
const countyInfoWindowContent=[
  "<div class='info'>",
  "<div id='infotext' style='text-align: center;font-size:12px '>",
  "</div><hr>",
  "<div id='adcode-info'>行政编码：</div>",
  "<div id='markerDialect'>方言：",
  "<div id='dialects'></div>",
  "<input type='text' id='language-info' class='info-text' disabled>",
  "<input type='text' id='dialect-info' class='info-text' disabled>",
  "<input type='text' id='subdialect-info' class='info-text' disabled>",
  "<input type='text' id='accent-info' class='info-text' disabled>",
  "<a id='add-dialect'>添加➕（请用菜单里的方言筛选框输入）</a>",
  "<a id='clear-dialect-info'>清空❌</a>",
  "</div>",
  "<div id='buttoncontainer'>",
  "<button id='closeWindow'>关闭❎</button>",
  "</div>",
  "</div>",];

let infoWindow,countyInfoWindow;
function menuOnClick()
{
  console.log(isShowingMenu.value)
  isShowingMenu.value = !isShowingMenu.value;
}
function clearSelectionBox(needClearInfo=true)
{
  selectedLanguage.value = ({lname: null,lid:24})
  selectedDialect.value = ({dname: null,did:35})
  selectedSubdialect.value = ({sname: null,sid:34})
  selectedAccent.value = ({aname: null,aid:260})
  if(document.getElementById("language-info")&&needClearInfo)
  {
    document.getElementById("language-info").value="";
    document.getElementById("dialect-info").value="";
    document.getElementById("subdialect-info").value="";
    document.getElementById("accent-info").value="";
  }
}
function clearPolygon(needCleanSelections) {
  if (needCleanSelections) {
    clearSelectionBox()
  }
  closeCountyInfoWindow()
  // props.map.remove(polygons)
  selectedArea.setAdcode([])
  // props.map.remove(selectedArea);
  selectedAdcodes.length=0;
  for (let marker of markers) {
    marker.marker.setIcon(new AMap.Icon({
      size: new AMap.Size(25, 34),
      image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
      imageSize: new AMap.Size(25, 34)
    }));
  }
  polygons.length=0;
}

function onLInput(e)
{
  if (e.inputType === 'deleteContentBackward' ||
      e.inputType === 'deleteContentForward') {
    selectedLanguage.value = {lname: null,lid:24}
    document.getElementById("language-info").value="暂无数据";
  }
}
function onDInput(e)
{
  if (e.inputType === 'deleteContentBackward' ||
      e.inputType === 'deleteContentForward') {
    selectedDialect.value = {dname: null,did:35};
    document.getElementById("dialect-info").value="暂无数据";
  }
}
function onSInput(e)
{
  if (e.inputType === 'deleteContentBackward' ||
      e.inputType === 'deleteContentForward') {
    selectedSubdialect.value = {sname: null,sid:34};
    document.getElementById("subdialect-info").value="暂无数据";
  }
}
function onAInput(e)
{
  if (e.inputType === 'deleteContentBackward' ||
      e.inputType === 'deleteContentForward') {
    selectedAccent.value = {aname: null,aid:260};
    document.getElementById("accent-info").value="暂无数据";
  }
}

async function accentOnClick() {
  let res;
  if(selectedSubdialect.value.sname)
    res = await request.get('/accent/accents/' + selectedSubdialect.value.sid)
  else
    res = await request.get('/accent/accents/')
  accents.value = res.data;
  accents.value=accents.value.filter(o =>
      o.aname!=='暂无数据'
  )
  languages.value = [];
  dialects.value = [];
  subdialects.value = [];
}
function accentLiOnClick(accent)
{
  selectedAccent.value=accent;
  accents.value=[];
  selectedSubdialect.value=accent.subDialect;
  selectedDialect.value=accent.subDialect.dialect;
  selectedLanguage.value=accent.subDialect.dialect.language;
  document.getElementById("accent-info").value=accent.aname
  document.getElementById("subdialect-info").value=selectedSubdialect.value.sname
  document.getElementById("dialect-info").value=selectedDialect.value.dname
  document.getElementById("language-info").value=selectedLanguage.value.lname
}
async function subDialectOnClick() {
  let res;
  if(selectedDialect.value.dname)
    res = await request.get('/accent/subdialects/' + selectedDialect.value.did)
  else
    res = await request.get('/accent/subdialects/')
  subdialects.value = res.data;
  subdialects.value=subdialects.value.filter(o =>
      o.sname!=='暂无数据'
  )
  languages.value = [];
  dialects.value = [];
  accents.value = [];
}
function subDialectLiOnClick(subdialect)
{
  selectedSubdialect.value=subdialect;
  subdialects.value=[];
  selectedDialect.value=subdialect.dialect;
  selectedLanguage.value=subdialect.dialect.language;
  selectedAccent.value={aname:null,aid:260};
  document.getElementById("subdialect-info").value=subdialect.sname
  document.getElementById("dialect-info").value=selectedDialect.value.dname
  document.getElementById("language-info").value=selectedLanguage.value.lname
  document.getElementById("accent-info").value="暂无数据";
}
function dialectLiOnClick(dialect)
{
  selectedDialect.value=dialect;
  dialects.value=[];
  selectedLanguage.value=dialect.language;
  selectedSubdialect.value={sname:null,sid:34};
  selectedAccent.value={aname:null,aid:260}
  document.getElementById("dialect-info").value=dialect.dname
  document.getElementById("language-info").value=selectedLanguage.value.lname
  document.getElementById("subdialect-info").value="暂无数据";
  document.getElementById("accent-info").value="暂无数据";
}
async function dialectOnClick() {
  let res;
  if(selectedLanguage.value.lname)
    res = await request.get('/accent/dialects/'+selectedLanguage.value.lid)
  else
    res = await request.get('/accent/dialects/')
  dialects.value = res.data;
  dialects.value=dialects.value.filter(o =>
      o.dname!=='暂无数据'
  )
  languages.value = [];
  subdialects.value = [];
  accents.value = [];

}
function languageLiOnClick(language)
{
  selectedLanguage.value=language;
  languages.value=[];
  selectedDialect.value={dname:null,did:35};
  selectedSubdialect.value={sname:null,sid:34};
  selectedAccent.value={aname:null,aid:260};
  document.getElementById("dialect-info").value="暂无数据";
  document.getElementById("subdialect-info").value="暂无数据";
  document.getElementById("accent-info").value="暂无数据";
  document.getElementById("language-info").value=language.lname
}

async function languageOnClick() {
  const res = await request.get('/accent/languages')
  //console.log(res);
  languages.value = res.data;
  languages.value=languages.value.filter(o =>
      o.lname!=='暂无数据'
  )
  dialects.value = [];
  subdialects.value = [];
  accents.value = [];
}

async function findCountiesByDialects()
{
  // clearPolygon(false)
  selectedArea.setAdcode([])
  polygons.length=0
  // props.map.remove(selectedArea);
  for (let marker of markers) {
    marker.marker.setIcon(new AMap.Icon({
      size: new AMap.Size(25, 34),
      image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
      imageSize: new AMap.Size(25, 34)
    }));
  }

   const counties=(await request.get("/accent/counties", {
    params: {
      language: selectedLanguage.value.lname,
      dialect: selectedDialect.value.dname,
      subDialect: selectedSubdialect.value.sname,
      accent: selectedAccent.value.aname,
    }
  })).data
  selectedAdcodes.length=0;
  for(let county of counties)
  {
    let paths=JSON.parse(county.polygon).features[0].geometry.coordinates[0];
    selectedAdcodes.push(county.adcode);
    getPolygonFromDatabase(paths,0)
  }
  selectedArea.setAdcode(selectedAdcodes)
  // props.map.add(selectedArea)
  props.map.setFitView(polygons);

  let findMarkers=(await request.get("/marker/markers/accent", {
    params: {
      language: selectedLanguage.value.lname,
      dialect: selectedDialect.value.dname,
      subdialect: selectedSubdialect.value.sname,
      accent: selectedAccent.value.aname,
    }
  })).data
  for(let findMarker of findMarkers)
  {
    let fullMarker=null;
    for(let marker of markers)
    {
      if(marker.mid===findMarker.mid)
      {
        fullMarker=marker;
        break
      }
    }
    //console.log("index")
    //console.log(fullMarker)
    if(fullMarker)
      fullMarker.marker.setIcon(new AMap.Icon({
        size: new AMap.Size(25, 34),
        image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png',
        imageSize: new AMap.Size(25, 34)
      }));
  }
}

function getPolygon(adcode,paths) {
  return new Promise(resolve => {
    district.search(adcode, (status, result) =>{
      if (status === "complete") {
        // if (needClear) clearPolygon(false);
        const bounds = result.districtList[0].boundaries;
        // console.log(adcode)
        console.log(bounds)
        if (bounds) {
          bounds.forEach(path => {
            paths.push(path)
          });
        }
      }
      resolve();
    });
  });
}

function getPolygonFromDatabase(paths, needClear)
{
  if (needClear) clearPolygon(false);
  if(paths.length) {
    paths.forEach(path => {
      const polygon = new AMap.Polygon({
        path,
        strokeWeight: 1,
        fillOpacity: 0.4,
        fillColor: "#80d8ff",
        strokeColor: "#0091ea"
      });
      polygons.push(polygon);
    })

    // props.map.add(polygons);
  }
}

function selectOff()
{
  isSelectOn=0;
  document.getElementById("select").textContent="框选✍️";
  // clearPolygon(false);
}
function selectOn()
{
  isSelectOn=1;
  document.getElementById("select").textContent="鼠标🖱️";
}
function rulerOff()
{
  isRulerOn=0;
  document.getElementById("ruler").textContent="测距📏";
  // deleteRuler();
}
function rulerOn()
{
  // ruler.turnOn();

  toastRef.value.showMessage('🔔点击红色的端点删除一整条折线');
  document.getElementById("ruler").textContent="鼠标🖱️";
  isRulerOn=1;
}
function markOff()
{
  isMarkOn=0;
}
function markOn()
{
  isMarkOn=1;
}
function searchKeyword() {
  let keyword=searchInput.value.value;
  district.search(keyword, (status, result) => {
    if (
        status === 'complete' &&
        result.districtList &&
        result.districtList.length > 0 &&
        result.districtList[0].level!=='street'
    ) {
      // drawDistrict(result.districtList[0])
      console.log(result.districtList[0].level)
      const bounds = result.districtList[0].boundaries
      if (!bounds) return
      polygons.length=0;
      bounds.forEach(boundary => {
        polygons.push(
          new AMap.Polygon({
            path: boundary,
            strokeColor: "#FF33FF",
            strokeWeight: 2,
            fillOpacity: 0.2,
            fillColor: "#1791fc",
          })
        )
      })
      selectedArea.setAdcode(result.districtList[0].adcode);
      props.map.setFitView(polygons);
    } else {
      searchPlace(keyword)
    }
  })
}
function searchPlace(address) {
  AMap.plugin("AMap.Geocoder", function () {
    // if(address.contains())
    // const geocoder = new AMap.Geocoder()
    if (address === '') return

    geocoder.getLocation(address, async (_, result) => {
      if (!result.geocodes.length) return

      const lnglat = result.geocodes[0].location

      await addMarker(null, lnglat,'',null,true,true)
    })
  })
}
function closeInfoWindow()
{
  infoWindow.close();

}
function closeCountyInfoWindow()
{
  countyInfoWindow.close();

}
function closeDropdown()
{
  languages.value=[];
  dialects.value=[];
  subdialects.value=[];
  accents.value=[];
}
async function deleteMarker(marker)
{
  infoWindow.close();
  props.map.remove(marker.marker);
  let index=markers.indexOf(marker)
  if(index!==-1)
  {
    markers.splice(index,1);
    await request.delete("/marker/marker/"+marker.mid)
  }
  //console.log(markers);
}

function rulerOnClick()
{
  if(isRulerOn) {
    rulerOff();
  }
  else {
    rulerOn();
    markOff();
    selectOff();
  }
}
function markOnClick()
{
  markOn();
  selectOff();
  rulerOff();
}
function selectOnClick()
{
  if(isSelectOn)
  {
    selectOff();
    clearPolygon(false);
  }
  else
  {
    markOff();
    rulerOff();
    selectOn();
  }
}
function computeDis(p1,p2,text){
  // let p1 = m1.getCenter();
  // let p2 = m2.getCenter();
  let textPos = p1.divideBy(2).add(p2.divideBy(2));
  let distance = Math.round(p1.distance(p2));
  text.setText(distance/1000+'km')
  text.setPosition(textPos)
}

async function saveInfo(mid,fullMarker)
{
  let remark=document.getElementById("remark").value;
  let lname=document.getElementById("language-info").value;
  let dname=document.getElementById("dialect-info").value;
  let sname=document.getElementById("subdialect-info").value;
  let aname=document.getElementById("accent-info").value;
  //console.log(remark+" "+lname+" "+dname+" "+sname+" "+aname)
  await request.put("/marker/marker",{
    mid:mid,
    remark:remark,
    lname:lname,
    dname:dname,
    sname:sname,
    aname:aname
  })
  let index=markers.indexOf(fullMarker);
  markers[index].remark=remark;
  markers[index].lname=lname;
  markers[index].dname=dname;
  markers[index].sname=sname;
  markers[index].aname=aname;
  closeInfoWindow()
  clearSelectionBox()
}

async function addMarker(mid,lnglat,remark='', accent = null,needAddToDatabase=true,needFit=false) {
  let marker = new AMap.Marker({
    icon:new AMap.Icon({
      size: new AMap.Size(25, 34),
      image: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
      imageSize: new AMap.Size(25, 34)
    })
  });
  let adcode,address;
  await geocoder.getAddress(lnglat, async (status, result) => {
    if (status === 'complete' && result.regeocode) {
      adcode = result.regeocode.addressComponent.adcode;
      address = result.regeocode.formattedAddress;
      if (!accent) {
        accent = (await request.get('/accent/accent/' + adcode)).data;
        if(accent.length===0)
        {
          accent={
            lid:24,
            did:35,
            sid:34,
            aid:260,
            lname:'暂无数据',
            dname:'暂无数据',
            sname:'暂无数据',
            aname:'暂无数据'
          }
        }
        else
          accent = accent[0]
      }
      let fullMarker = {
        mid:mid,
        marker: marker,
        remark: remark,
        lid: accent.lid,
        did: accent.did,
        sid: accent.sid,
        aid: accent.aid,
        lname: accent.lname,
        dname: accent.dname,
        sname: accent.sname,
        aname: accent.aname,
      };
      props.map.add(marker);
      marker.setPosition(lnglat);
      markers.push(fullMarker);
      if(needAddToDatabase){
        let newMarker=markers[markers.length-1]
          mid=(await request.post("/marker/marker", {
          lng: lnglat.lng,
          lat: lnglat.lat,
          remark: newMarker.remark,
          lid: newMarker.lid,
          did: newMarker.did,
          sid: newMarker.sid,
          aid: newMarker.aid,
        })).data;
        markers[markers.length-1].mid=mid;
        fullMarker.mid=mid;
      }
      if(needFit)
        props.map.setFitView(marker)
      markers[markers.length-1].marker.on('click', (e) => {
        // let geocoder=new AMap.Geocoder();
        infoWindow.open(props.map, lnglat);
        clearSelectionBox();
        document.getElementById("clear-dialect-info").onclick=()=>{
          document.getElementById("language-info").value="暂无数据";
          document.getElementById("dialect-info").value="暂无数据";
          document.getElementById("subdialect-info").value="暂无数据";
          document.getElementById("accent-info").value="暂无数据";
          clearSelectionBox(false)
        }
        document.getElementById("infotext").textContent = address;
        document.getElementById("language-info").value=fullMarker.lname;
        document.getElementById("dialect-info").value=fullMarker.dname;
        document.getElementById("subdialect-info").value=fullMarker.sname;
        document.getElementById("accent-info").value=fullMarker.aname;
        document.getElementById("position").textContent = "坐标：" + lnglat;
        document.getElementById("remark").value=fullMarker.remark;
        document.getElementById("deleteMarker").onclick = () => {
          deleteMarker(fullMarker);
        };
        document.getElementById("changeInfo").onclick=()=>{
          saveInfo(mid,fullMarker)
        }
        document.getElementById("closeWindow").onclick = ()=>{
          // clearSelectionBox();
          closeInfoWindow();
          clearSelectionBox()
        }
      });
    }
    else
    {
      await addMarker(mid,lnglat, remark, accent, needAddToDatabase, needFit);
    }
  })

}


onMounted(async () => {
  selectedArea = new AMap.DistrictLayer.Province({
    zIndex: 10,
    zooms: [2, 15],
    adcode: [],
    depth: 2,
    opacity: 0.4,
  });

  selectedArea.setStyles({
    "stroke-width": 2, //描边线宽
    fill: function (data) {
      return "rgb(180,250,200)";
    },
    "county-stroke": "rgb(100,180,160)",
  });


  props.map.add(selectedArea);
  AMap.plugin("AMap.Geocoder", () => {
    geocoder = new AMap.Geocoder();
  })
  infoWindow = new AMap.InfoWindow({
    isCustom: true,
    content: infoWindowContent.join('<br>'),
    offset: new AMap.Pixel(16, -45),
  });
  countyInfoWindow = new AMap.InfoWindow({
    isCustom: true,
    content: countyInfoWindowContent.join('<br>'),
    offset: new AMap.Pixel(16, -45),
  });
  let tempMarkers=(await request.get('/marker/markers/')).data;
  for(let tempMarker of tempMarkers)
  {
    //console.log(tempMarker)
    await addMarker(tempMarker.mid, [tempMarker.lng, tempMarker.lat], tempMarker.remark, {
      lid: tempMarker.lid,
      did: tempMarker.did,
      sid: tempMarker.sid,
      aid: tempMarker.aid,
      lname: tempMarker.lname,
      dname: tempMarker.dname,
      sname: tempMarker.sname,
      aname: tempMarker.aname
    },false);
  }
  AMap.plugin("AMap.AutoComplete", () => {
    new AMap.AutoComplete({
      input: searchInput.value
    })
  })
  AMap.plugin("AMap.DistrictSearch", () => {
    district = new AMap.DistrictSearch({
      level: "district", extensions: "all", subdistrict: 0
    });
  });
  props.map.on('mousemove',(e)=>{
    if (!segmentHeads.length) return
    if(!segmentHeads[segmentHeads.length - 1])return;
    let last = segmentHeads[segmentHeads.length - 1].next
    if(!last)last=segmentHeads[segmentHeads.length - 1]
    if (!last.segment) return
    const start = last.segment.getPath()[0]
    last.segment.setPath([
      start,
      e.lnglat
    ])
    if(textHeads[textHeads.length-1].next)
      computeDis(start,e.lnglat,textHeads[textHeads.length-1].next.text)
    else
      computeDis(start,e.lnglat,textHeads[textHeads.length-1].text)
  })
  props.map.on('click', async (e) => {
    closeDropdown()
    if (isMarkOn) {
      let lnglat = e.lnglat;
      await addMarker(null,lnglat)

      markOff();
    } else if (isSelectOn) {
      let lnglat = e.lnglat;

      await geocoder.getAddress(lnglat, async (status, result) => {
        if (status === 'complete' && result.regeocode) {
          let paths=[]

          let counties=(await request.get("/county", {
            params: {
              adcode: result.regeocode.addressComponent.adcode
            }
          })).data
          let address=result.regeocode.addressComponent;
          if(counties.length===0)
          {
            await getPolygon(result.regeocode.addressComponent.adcode, paths);
            const geoJson = {
              type: "FeatureCollection",
              features: [
                {
                  type: "Feature",
                  properties: {adcode:result.regeocode.addressComponent.adcode},
                  geometry: {
                    type: "Polygon",
                    coordinates: [paths]
                  }
                }
              ]
            };
            await request.post("/county",{
              cname:address.district,
              adcode:result.regeocode.addressComponent.adcode,
              polygon:btoa(String.fromCharCode.apply(null, new Uint8Array(pako.gzip(JSON.stringify(geoJson))))),
            })
          }
          else
          {
            paths=JSON.parse(counties[0].polygon).features[0].geometry.coordinates[0];
          }

          selectedAdcodes.length=0
          selectedAdcodes.push(result.regeocode.addressComponent.adcode)
          console.log(selectedAdcodes)
          getPolygonFromDatabase(paths,1)
          selectedArea.setAdcode(result.regeocode.addressComponent.adcode)
          console.log(selectedArea)
          // props.map.add(selectedArea)
          props.map.setFitView(polygons);

          countyInfoWindow.open(props.map,lnglat)
          clearSelectionBox();
          document.getElementById("language-info").value="";
          document.getElementById("dialect-info").value="";
          document.getElementById("subdialect-info").value="";
          document.getElementById("accent-info").value="";
          document.getElementById("infotext").textContent=address.province+address.city+address.district;
          document.getElementById("adcode-info").textContent="行政编码："+result.regeocode.addressComponent.adcode;
          document.getElementById("clear-dialect-info").onclick=()=>{
            clearSelectionBox();
          }
          document.getElementById("closeWindow").onclick=()=>{
            // clearSelectionBox();
            clearSelectionBox()
            closeCountyInfoWindow();
          }
          let countyDialects = (await request.get("/accent/accent/" + result.regeocode.addressComponent.adcode)).data;
          document.getElementById("dialects").textContent = "";

          const refreshCountyInfo=async (countyDialect) => {
            const deleteA = document.createElement("a")
            deleteA.textContent = "删除"
            deleteA.class = "deleteDialect"
            deleteA.onclick = async () => {
              //console.log(countyDialect)
              await request.delete(`/accent/county-dialect/${countyDialect.cdid}`)
              document.getElementById("dialects").textContent="";
              countyDialects = (await request.get("/accent/accent/" + result.regeocode.addressComponent.adcode)).data;
              for(let countyDialect of countyDialects)
              {
                await refreshCountyInfo(countyDialect)
              }
            };
            const textNode = document.createTextNode(countyDialect.lname +
                " " + countyDialect.dname + " " + countyDialect.sname +
                " " + countyDialect.aname);
            document.getElementById("dialects").appendChild(textNode)
            document.getElementById("dialects").appendChild(deleteA)
            document.getElementById("dialects").appendChild(document.createElement("br"))
          }
          for(let countyDialect of countyDialects)
          {
            await refreshCountyInfo(countyDialect)
          }
          document.getElementById("add-dialect").onclick=async ()=>{
            if(selectedLanguage.value.lname!=null) {
              await request.post("/accent/county/" + result.regeocode.addressComponent.adcode+"/accent",{
                lid:selectedLanguage.value.lid,
                did:selectedDialect.value.did,
                sid:selectedSubdialect.value.sid,
                aid:selectedAccent.value.aid,
              });
              document.getElementById("dialects").textContent="";
              countyDialects = (await request.get("/accent/accent/" + result.regeocode.addressComponent.adcode)).data;
              for(let countyDialect of countyDialects)
              {
                await refreshCountyInfo(countyDialect)
              }
            }
          }
        }
      });

    } else if (isRulerOn) {
      if(!isDrawingSegment)
      {
        isDrawingSegment=true;
        let circleMarker = new AMap.CircleMarker({
          center: e.lnglat,
          radius: 7,
          strokeColor: 'red',
          strokeWeight: 3,
          strokeOpacity: 0.5,
          fillColor: 'red',
          zIndex: 10,
          bubble: false,
          cursor: 'pointer',
          clickable: true,
          draggable: true
        })
        let polyline = new AMap.Polyline({
          path: [e.lnglat,e.lnglat],
          strokeWeight: 2, //线条宽度
          strokeColor: "red", //线条颜色
          lineJoin: "round", //折线拐点连接处样式
        });
        let text = new AMap.Text({
          text: '',
          style: {
            'background-color': 'white',
            'border-color': '#000',
            'font-size': '12px'
          }
        });

        let segment={
          segment:polyline,
          text:text,
          next:null,
        }
        let point={
          point:circleMarker,
          next:null,
          lastSegment:null,
          nextSegment:segment
        }

        if(!segmentHeads.length) {
          segmentHeads.push(segment)
        }
        else {
          segmentHeads[segmentHeads.length-1]=segment;
        }
        props.map.add(segment.segment);
        props.map.add(point.point);

        pointHeads.push(point)
        let textNode={
          text:text,
          next:null
        }

        textHeads.push(textNode)
        //  TODO
        props.map.add(textNode.text)
        point.point.on('click',()=>{
          let p=point;
          while(p!==null)
          {
            let t=p;
            p=p.next;
            props.map.remove(t.point);
            t=null;
          }
          p=segment;
          while(p!==null)
          {
            let t=p;
            p=p.next;
            props.map.remove(t.segment);
            t=null;
          }
          p=textNode;
          while(p!==null)
          {
            let t=p;
            p=p.next;
            props.map.remove(t.text);
            t=null;
          }
          // props.map.remove(texts);
          // texts.length=0
        })
        addOnDragging(point)
      }
      else {
        if(textHeads[textHeads.length-1].next)
        {
          props.map.remove(textHeads[textHeads.length-1].next.text)
        }
        else
        {
          props.map.remove(textHeads[textHeads.length-1].text)
        }
        let circleMarker = new AMap.CircleMarker({
          center: e.lnglat,
          radius: 7,
          strokeColor: 'red',
          strokeWeight: 3,
          strokeOpacity: 0.5,
          fillColor: 'yellow',
          zIndex: 10,
          bubble: true,
          cursor: 'pointer',
          clickable: true,
          draggable: true
        })
        let polyline = new AMap.Polyline({
          path: [e.lnglat,e.lnglat],
          strokeWeight: 2, //线条宽度
          strokeColor: "red", //线条颜色
          lineJoin: "round", //折线拐点连接处样式
        });
        let text = new AMap.Text({
          text: '',
          style: {
            'background-color': 'white',
            'border-color': '#000',
            'font-size': '12px'
          }
        });

        let segment={
          segment:polyline,
          text:text,
          next:segmentHeads[segmentHeads.length-1].next
        }
        let point={
          point:circleMarker,
          next: pointHeads[pointHeads.length-1].next,
          lastSegment:null,
          nextSegment:segment,
        }
        // console.log(texts)
        textHeads[textHeads.length-1].next={
          text:text,
          next:textHeads[textHeads.length-1].next
        }
        //TODO
        props.map.add(text)
        props.map.add(segment.segment);
        props.map.add(point.point);
        pointHeads[pointHeads.length-1].next=point;
        const currentHead=segmentHeads[segmentHeads.length-1]
        if(currentHead.next)
        {
          const lastSegment=currentHead.next;
          point.lastSegment=lastSegment;
          const start=lastSegment.segment.getPath()[0]
          computeDis(start,e.lnglat,point.lastSegment.text)
          props.map.add(point.lastSegment.text)
          segmentHeads[segmentHeads.length-1].next=segment
          lastSegment.segment.setPath([start,e.lnglat])
        }
        else
        {
          const lastSegment=currentHead;
          console.log(lastSegment)
          // lastSegment.text=text;
          point.lastSegment=lastSegment;
          const start=lastSegment.segment.getPath()[0]
          computeDis(start,e.lnglat,point.lastSegment.text)
          props.map.add(point.lastSegment.text)
          segmentHeads[segmentHeads.length-1].next=segment
          lastSegment.segment.setPath([start,e.lnglat])
        }
        addOnDragging(point)
      }

    }
  })
  props.map.on('rightclick', () => {
    // deleteRuler();
    isDrawingSegment=false;
    if(!segmentHeads.length)return
    let lastSegment=segmentHeads[segmentHeads.length-1].next;
    if(!segmentHeads[segmentHeads.length-1].next)
    {
      lastSegment=segmentHeads[segmentHeads.length-1]
    }
    segmentHeads.push(null)
    props.map.remove(lastSegment.segment);

    if(!segmentHeads[segmentHeads.length-2].next) {
      segmentHeads[segmentHeads.length-2]=lastSegment.next;
      pointHeads[pointHeads.length-1].nextSegment=null;
    }
    else {
      segmentHeads[segmentHeads.length-2].next=lastSegment.next
      pointHeads[pointHeads.length-1].next.nextSegment=null;
    }
    if(textHeads[textHeads.length-1].next)
    {
      props.map.remove(textHeads[textHeads.length-1].next.text);
    }
    else
    {
      props.map.remove(textHeads[textHeads.length-1].text);
    }
  });

})
function addOnDragging(point) {
  point.point.on('dragging', () => {
    if(point.lastSegment)
    {
      const lastSeg=point.lastSegment.segment
      const start = lastSeg.getPath()[0];
      lastSeg.setPath([start,point.point.getCenter()])
      computeDis(start,point.point.getCenter(),point.lastSegment.text)
    }
    if(point.nextSegment)
    {
      const nextSeg=point.nextSegment.segment
      const end = nextSeg.getPath()[1];
      nextSeg.setPath([point.point.getCenter(),end])
      computeDis(point.point.getCenter(),end,point.nextSegment.text)
    }
  })
}
</script>

<style scoped>
label{
  margin-left: 20px;
}
.dropdown{
  margin-right: 10px;
}

</style>