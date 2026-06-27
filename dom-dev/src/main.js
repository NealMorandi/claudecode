import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import ja from 'element-plus/es/locale/lang/ja'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import AppSelect from '@/components/AppSelect.vue'
import '@/assets/styles/index.css'
import '@/router/guard'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: ja })
app.component('AppSelect', AppSelect)
app.mount('#app')
