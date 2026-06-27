import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    announcement: {
      text: '',
      visible: false,
    },
  }),
  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    showAnnouncement(text) {
      this.announcement.text = text
      this.announcement.visible = true
    },
    dismissAnnouncement() {
      this.announcement.visible = false
    },
  },
})
