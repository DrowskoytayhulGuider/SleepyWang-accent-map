var map = new AMap.Map('container', {
    viewMode: '2D',
    zoom:11,
});
const searchBox=document.getElementById("searchtext");
const searchButton=document.getElementById("searchbutton");
var polygon=null;
function clearPolygon()
{
    if (polygon) {
        map.remove(polygon);
        polygon = null;
    }
}
function drawPolygon(city)
{
    AMap.plugin("AMap.DistrictSearch", function () {

        const district = new AMap.DistrictSearch({
            level: "district",
            extensions: "all",
            subdistrict: 0
        });
        console.log(district)
        district.search(city, (status, result) => {
            console.log(result);
            clearPolygon();
            let bounds = result.districtList[0].boundaries;
            console.log(bounds);
            if (bounds) {
                for (let i = 0; i < bounds.length; i += 1) {
                    bounds[i] = [bounds[i]];
                }
                console.log(bounds);
                polygon = new AMap.Polygon({
                    strokeWeight: 1,
                    path: bounds,
                    fillOpacity: 0.4,
                    fillColor: "#80d8ff",
                    strokeColor: "#0091ea"
                });
                map.add(polygon);
                map.setFitView(polygon);
                console.log("ok");
            }
        });

    });
}

let isSelectOn=0;
let ruler=null
document.getElementById("select").onclick=()=>{
    if(isSelectOn)
    {
        selectOff();
    }
    else
    {
        markOff();
        rulerOff();
        selectOn();
    }
};

let isRulerOn=0;
AMap.plugin("AMap.RangingTool", function () {
    ruler = new AMap.RangingTool(map);
    document.getElementById("ruler").onclick = () => {
        if(isRulerOn) {
            rulerOff();
        }
        else {
            rulerOn();
            markOff();
            selectOff();
        }
    };
});

function selectOff()
{
    isSelectOn=0;
    document.getElementById("select").textContent="框选✍️";
    clearPolygon();
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
    if(ruler)
        ruler.turnOff();
}
function rulerOn()
{
    ruler.turnOn();
    document.getElementById("ruler").textContent="鼠标🖱️";
    isRulerOn=1;
}
let isMarkOn=0;
function markOff()
{
    isMarkOn=0;


}
function markOn()
{
    isMarkOn=1;
}
document.getElementById("mark").onclick = () => {
    markOn();
    selectOff();
    rulerOff();
};
const countyInfoWindowContent=[
    "<div class='info'>",
    "<div id='infotext' style='text-align: center;font-size:15px '>",
    "</div><hr>",
    "<div id='markerDialect'>方言：",
    "<div id='doialects'></div>",
    "<input type='text' id='language-info' class='info-text' disabled>",
    "<input type='text' id='dialect-info' class='info-text' disabled>",
    "<input type='text' id='subdialect-info' class='info-text' disabled>",
    "<input type='text' id='accent-info' class='info-text' disabled>",
    "<a id='clear-dialect-info'>添加➕（请用地图最顶端的方言筛选框输入）</a>",
    "</div>",
    "<div id='buttoncontainer'>",
    "<button id='changeInfo'>保存💾️</button>",
    "<button id='closeWindow'>关闭❎</button>",
    "</div>",
    "</div>",];
let infoWindowContent=[
    "<div class='info'>",
    "<div id='infotext'>",
    "</div><hr>",
    "<div id='position'>坐标：</div>",
    "<div id='dialect'>方言：暂无数据</div>",
    "<div id='buttoncontainer'>",
    "<button id='deleteMarker'>删除🗑️</button>",
    "<button onclick='closeInfoWindow()'>关闭❎</button>",
    "</div>",
    "</div>",];
let infoWindow=new AMap.InfoWindow({
    isCustom:true,
    content:infoWindowContent.join('<br>'),
    offset: new AMap.Pixel(16,-45),
});
function closeInfoWindow()
{
    infoWindow.close();
}
let markers=[];
function deleteMarker(marker)
{
    infoWindow.close();
    map.remove(marker);
    let index=markers.indexOf(marker)
    if(index!==-1)
    {
        markers.splice(index,1);
    }
    console.log(markers);
}
AMap.plugin("AMap.Geocoder",()=>{
    searchButton.onclick=()=>{
        let address=searchBox.value;
        let geocoder=new AMap.Geocoder();
        geocoder.getLocation(address,(status,result)=>{
            if(status==='complete'&&result.geocodes.length){
                let lnglat=result.geocodes[0].location;
                let marker=new AMap.Marker();
                marker.setPosition(lnglat);
                map.add(marker);
                marker.on('click',(e)=>{
                    infoWindow.open(map,lnglat);
                    geocoder.getAddress(lnglat,(status,result)=>{
                        if(status==='complete'&&result.regeocode){
                            let address=result.regeocode.formattedAddress;
                            document.getElementById("infotext").textContent=address;
                            document.getElementById("position").textContent="坐标："+lnglat;
                        }
                    });
                    document.getElementById("deleteMarker").onclick=()=>{
                        deleteMarker(marker);
                    };
                })
                markers.push(marker);
                map.setFitView(marker);
            }
        });
    };
    map.on('click',(e)=>{
        if(isMarkOn)
        {
            let lnglat=e.lnglat;
            let marker=new AMap.Marker();
            map.add(marker);
            marker.setPosition(lnglat);
            marker.on('click',(e)=>{
                let geocoder=new AMap.Geocoder();
                infoWindow.open(map,lnglat);
                geocoder.getAddress(lnglat,(status,result)=>{
                    if(status==='complete'&&result.regeocode){
                        let address=result.regeocode.formattedAddress;
                        document.getElementById("infotext").textContent=address;
                        document.getElementById("position").textContent="坐标："+lnglat;
                    }
                });
                document.getElementById("deleteMarker").onclick=()=>{
                    deleteMarker(marker);
                };
            });
            markers.push(marker);
            markOff();
        }
        else if(isSelectOn)
        {
            let lnglat=e.lnglat;
            let geocoder = new AMap.Geocoder();
            geocoder.getAddress(lnglat, (status, result) => {
                if (status === 'complete' && result.regeocode) {
                    console.log(result.regeocode.addressComponent.adcode);
                    drawPolygon(result.regeocode.addressComponent.adcode);
                }
            });
        }
    });
});
AMap.plugin("AMap.AutoComplete",()=>{
    new AMap.AutoComplete({
        input:"searchtext",
    });
});

const data = {
    "西南官话": {
      "川黔片":{
          "成渝小片":[
              "成都市区","德阳","绵阳","江油-安县","绵竹","什邡",
              "广元","平昌-通江","苍溪","苍溪东-巴中-旺苍",
              "南充市区","武胜-南充城郊","仪陇","蓬安-营山","南部-阆中","达州-渠县",
              "宣汉-万源",
              "云万开","两巫一奉",
              "重庆","渝西-安岳东",
              "长寿-涪陵","黔江-彭水",
              "酉阳","秀山",
              "邻水-垫江-大竹山后",
              "开江-梁平-达县东",
              "丰都-忠县-石柱",
              "道真-正安-南川-武隆",
              "广安",
              "遂宁-乐至-安岳-苍山",
              "资阳-资中-连界-汪洋",
              "中江-三台-金堂南-简阳东-乐至西",
              "绥阳-凤冈-湄潭","城口",
              "毕节","雷波-永善-彝良-大关","威信-镇雄","铜仁"
          ],
          "黔中小片":[
              "贵阳","安顺","六盘水","黔西南"
          ],
          "陕南小片":["汉中-安康"]
      },
        "西蜀片":{
            "岷赤小片":[
                "成都河东-汶川","成都河西","邛蒲大崇",
                "新津-彭山北","彭山","眉山","眉山河东-青神",
                "洪雅","夹江-丹棱","峨眉-金口河-峨边","乐山-井研南",
                "犍为-黄丹-舟坝","沐川-马边","屏山-绥江","宜宾-南溪-水富",
                "高县-叙州南",
                "珙县-长宁","江安-泸州","兴文-叙永-古蔺",
                "合江-赤水-习水","永川南-江津","綦江-桐梓",
                "仁怀","遵义","务川","德江-印江-思南-沿河",
                "荥经","茂县-松潘","剑阁","盐亭-南部西北",
                "西充-嘉陵北","射洪-蓬溪","简阳","黑龙滩-龙正"
            ],
            "江贡小片":[
                "仁寿-井研","荣县",
                "威远-贡井-荣县东",
                "大安-内江南",
                "沿滩-富顺-隆昌南",
                "隆昌-泸县北",
                "内江市区-资中南",
                "宜宾北","泥溪-商州-龙华-沐川东-荍坝",
                "自贡市区","古蔺东-仁怀-习水南",
                "筠连-高县南-珙县南-盐津"
            ],
            "雅甘小片":[
                "雅安北",
                "天全","泸定-康定",
                "汉源-石棉","甘洛"
            ],
        },
        "川西片":{
            "康藏小片":[
                "藏区","小金-金川","理县",
            ],
            "凉山小片":[
                "越西","冕宁",
                "冕宁南-喜德",
                "西昌-德昌北",
                "攀枝花","米易",
                "盐边","会理-会东",
                "仁和务本-华坪",
                "仁和南-永仁","盐源-木里",
                "彝区"
            ],
        },
        "云南片":{
            "滇中小片":["昭通-巧家-鲁甸","昆明"],
        },
        "桂柳片":{
            "黔南小片":[
                "黄平-福泉",
                "凯里-麻江",
                "都匀-丹寨",
                "平塘-独山-三都",
                "荔波-榕江-从江"
            ]
        },
        "湖广片":{
            "黔东小片":[],
            "怀玉小片":["玉屏"],
            "黎靖小片":["黎平"],
        },
    },
    "湘语": {
        "未分片":{
            "方言岛":[
                "达县安仁","竹篙-积金-三台南","丹山-乐至东南-周礼-双桥-资中",
                "内江乐贤","白花-邱场-永兴","安岳石板","潼南龙形","长乐-顾县",
                "晴隆喇叭苗","天柱江东"
            ]
        }
    },
    "客家话":{
        "未分片":{
            "方言岛":[
                "成都东山","仪陇","西昌","隆昌-荣昌"
            ]
        }
    },
};

const language = document.getElementById("language");
const languageList = document.getElementById("languageList");

const dialect = document.getElementById("dialect");
const dialectList = document.getElementById("dialectList");

const subdialect = document.getElementById("subdialect");
const subdialectList = document.getElementById("subdialectList");

const accent = document.getElementById("accent");
const accentList = document.getElementById("accentList");

function showList(input, list, items, onSelect) {
    list.innerHTML = "";
    if (!items.length) {
        list.style.display = "none";
        return;
    }

    items.forEach(item => {
        const div = document.createElement("div");
        div.textContent = item;
        div.onclick = () => onSelect(item);
        list.appendChild(div);
    });

    list.style.display = "block";

}

function filter(items, keyword) {
    return items.filter(i => i.includes(keyword));
}

/* 一级搜索 */
language.addEventListener("input", () => {
    const keys = Object.keys(data);
    const result = filter(keys, language.value);
    showList(language, languageList, result, val => {
        language.value = val;
        languageList.style.display = "none";

        // 重置二级
        dialect.value = "";
        dialect.disabled = false;

        showList(dialect, dialectList, data[val], selectedDialect => {
            dialect.value = selectedDialect;
            dialectList.style.display = "none";
        });
    });
});

dialect.addEventListener("input", () => {
    const selectedLanguage = language.value;
    const keys=Object.keys(data[selectedLanguage]);
    if (!data[selectedLanguage]) return;

    const result = filter(keys, dialect.value);
    showList(dialect, dialectList, result, selectedDialect => {
        dialect.value = selectedDialect;
        dialectList.style.display = "none";

        subdialect.value = "";
        subdialect.disabled = false;

        showList(subdialect, subdialectList, data[selectedDialect], selectedSubdialect => {
            subdialect.value = selectedSubdialect;
            subdialectList.style.display = "none";
        });

    });
});

subdialect.addEventListener("input", () => {
    const selectedDialect = dialect.value;
    const keys=Object.keys(data[language.value][selectedDialect]);
    if (!data[language.value][selectedDialect]) return;

    const result = filter(keys, subdialect.value);
    showList(subdialect, subdialectList, result, selectedSubdialect => {
        subdialect.value = selectedSubdialect;
        subdialectList.style.display = "none";

        accent.value = "";
        accent.disabled = false;

        showList(accent, accentList, data[selectedSubdialect], selectedAccent => {
            accent.value = selectedAccent;
            accentList.style.display = "none";
        });

    });
});

accent.addEventListener("input", () => {
    const selectedSubdialect = subdialect.value;
    if (!data[language.value][dialect.value][selectedSubdialect]) return;
    const result = filter(data[language.value][dialect.value][selectedSubdialect], accent.value);
    showList(accent, accentList, result, selectedAccent => {
        accent.value = selectedAccent;
        accentList.style.display = "none";
    });
});

/* 点击外部关闭 */
document.addEventListener("click", e => {
    if (!e.target.closest(".dropdown")) {
        languageList.style.display = "none";
        dialectList.style.display = "none";

    }
});