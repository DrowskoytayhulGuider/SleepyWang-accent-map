import { createRouter, createWebHistory } from 'vue-router'

import Map from '@/views/Map.vue'
import Backend from '@/views/Backend.vue'
import LanguageList from '@/views/LanguageList.vue'
import DialectList from '@/views/DialectList.vue'
import SubDialectList from '@/views/SubDialectList.vue'
import AccentList from '@/views/AccentList.vue'

const routes = [
    {
        path: '/',
        name: 'Map',
        component: Map
    },
    {
        path: '/backend',
        name: 'Backend',
        component: Backend
    },
    {
        path: '/languages',
        name: 'LanguageList',
        component: LanguageList,
    },
    {
        path: '/dialects/:id?',   // 动态路由
        name: 'DialectList',
        component: DialectList,
        props: true
    }
    ,
    {
        path: '/subdialects/:id?',   // 动态路由
        name: 'SubDialectList',
        component: SubDialectList,
        props: true
    }
    ,
    {
        path: '/accents/:id?',   // 动态路由
        name: 'AccentList',
        component: AccentList,
        props: true
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router