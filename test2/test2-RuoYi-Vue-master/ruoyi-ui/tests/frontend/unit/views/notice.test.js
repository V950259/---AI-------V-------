import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Notice from '@/views/system/notice/index.vue'
import store from '@/store'
import router from '@/router'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Notice Management', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Notice, {
      localVue,
      store,
      router
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：通知公告页面渲染', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('正常场景：通知公告列表数据', () => {
    wrapper.setData({
      noticeList: [
        { noticeId: 1, noticeTitle: '测试公告1', noticeType: '1' },
        { noticeId: 2, noticeTitle: '测试公告2', noticeType: '2' }
      ]
    })
    expect(wrapper.vm.noticeList.length).toBe(2)
  })

  it('边界场景：空通知公告列表', () => {
    wrapper.setData({ noticeList: [] })
    expect(wrapper.vm.noticeList.length).toBe(0)
  })

  it('边界场景：超长公告标题', () => {
    const longTitle = 'A'.repeat(500)
    wrapper.setData({
      noticeList: [
        { noticeId: 1, noticeTitle: longTitle, noticeType: '1' }
      ]
    })
    expect(wrapper.vm.noticeList[0].noticeTitle.length).toBe(500)
  })
})

