const app = getApp();

const formatUtil = require('../../../../utils/formatutil');
Page({
  data: {
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    UPLOAD_IMAGE: app.globalData.UPLOAD_IMAGE,
    THUMBNAIL: app.globalData.THUMBNAIL,
    ARTWORK: app.globalData.ARTWORK,
    ARTWORKNOWATER: app.globalData.ARTWORKNOWATER,
    isCard: 'no-card',
    ColorList: app.globalData.ColorList,
    fullReplys: [],
    topicdetails: '',
    topicIndex: -1,
    showHome: false,
    parentReplyId: 0,
    isLoad: true,
    hasReply: 'none', //空评论标志
    navigateButtonList: [], //贴子中链接跳转列表
    regionCode:'',
    onReachBottomLoading: false,
    nextPage: false,
    showLoad: true,

    // 收藏举报提示action
    showActionsheet: false,
    actionGroups: [],

    actionCommentGroups: [{
      text: '复制',
      value: 0
    }],

    topicId: '',
    SHAREHOSTURL: '', //朋友圈分享的页面使用这个主机地址
    isAnon: '' //是否调用匿名接口

  },
  onLoad(options) {
    console.log(options)
    var that = this
    let topicdetailsTmp = {}

    var obj = wx.getLaunchOptionsSync()
    console.log(obj)
    if (obj.scene == 1154) { //从朋友圈看到的单页面
      topicdetailsTmp.id = options.topicId
      that.data.topicId = options.topicId
      that.setData({
        topicdetails: topicdetailsTmp,
      })

      that.data.SHAREHOSTURL = app.globalData.HOSTURL
      that.data.isAnon = '/anon'
      that.getBbsTopicById(that.data.SHAREHOSTURL)
    } else {
      that.data.SHAREHOSTURL = app.globalData.HOSTURL
      // url传参检测
      if (undefined != options.topicId) {
        topicdetailsTmp.id = options.topicId
        that.setData({
          topicdetails: topicdetailsTmp,
          showHome: true,
          regionCode: options.regionCode
        })
      } else {
        // 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
        const eventChannel = this.getOpenerEventChannel()
        eventChannel.on('acceptDataFromOpenerPage', function (data) {
          that.setData({
            topicIndex: data.data.bindex,
            topicdetails: data.data.topicitem,
          })
        })
      }
      // 获取token
      app.getFirstLoginToken().then(res => {
        that.getBbsTopicById(that.data.SHAREHOSTURL)
      })
    }

  },
  onShow(e) {
    var that = this
    that.setData({
      showEmojiPanel: false,
      InputBottom: 0
    })
  },
  isCard(e) {
    this.setData({
      isCard: e.detail.value
    })
  },
  // 触底
  onReachBottom: function () {
    var that = this
    that.setData({
      nextPage: true
    })
  },
  onPullDownRefresh() {
    var that = this
    this.data.showLoad = true
    this.setData({
      nextPage: false
    })
    this.data.onReachBottomLoading = false
    this.data.navigateButtonList = ''
    this.data.parentReplyId = ''
    this.data.beReply = ''
    this.data.InputBottom = 0
    // this.data.topicdetails = ''
    this.data.fullReplys = []
    this.data.isPullRefresh = true
    this.getBbsTopicById(that.data.SHAREHOSTURL)
  },
  // mark:获取帖子详情
  getBbsTopicById(SHAREHOSTURL) {
    var that = this
    that.setData({
      getTopicFlag: false
    })
    let url = SHAREHOSTURL + '/bbs/bbsTopic/wise/mini/fullListById' + that.data.isAnon + '?topicId=' + this.data.topicdetails.id
    app.wxRequest('get', url, '').then(res => {
      if (res.data.code == 200) {
        if (res.data.result != null) {
          let result = res.data.result
          result.userRole = result.userRole.substring(4)
          // 添加动画属性
          result.exeCuteAnimation = result.userIsPraise

          if (result.createTime) {
            result.createTime = formatUtil.showDate(new Date(result.createTime.replace(/-/g, '/')))
          }
          if (result.updateTime) {
            result.updateTime = formatUtil.showDate(new Date(result.updateTime.replace(/-/g, '/')))
          }
          if (result.publicTime) {
            result.publicTime = formatUtil.showDate(new Date(result.publicTime.replace(/-/g, '/')))
          }
          if (result.editTime) {
            result.editTime = formatUtil.showDate(new Date(result.editTime.replace(/-/g, '/')))
          }

          // content 插入标志规则 !_SkipTopic_西安文理学院教务处_13453091178306887691_!
          // 正则表达式替换内容 获取指定字符串之后：  (?<=指定字符串)获取指定字符串之前： (?=指定字符串)，实现
          // /(?<=!_).+(?=_!)/
          // 生成按钮列表

          let navigateButtonListTmp = []
          let navBtnList = result.content.match(/(?=!_).+(?:_!)+/g);
          // let navBtnList = new RegExp().group().exec()
          // --------
          // if (null != navBtnList) {
          //   navBtnList.forEach(item => {
          //     let navBtnTmp = {}
          //     let navBtnList = item.split("_")
          //     navBtnTmp.title = navBtnList[2]
          //     navBtnTmp.topicId = navBtnList[3]
          //     navigateButtonListTmp.push(navBtnTmp)
          //   })
          //   // console.log(navBtnList)
          //   // console.log(navigateButtonListTmp)
          //   that.setData({
          //     navigateButtonList: navigateButtonListTmp
          //   })
          //   result.content = result.content.replace(/(?=!_).+(?:_!)/g, '')
          //   // result.content = result.content.replace("<p></p>", '')
          //   result.content = result.content.replace(new RegExp('<p></p>', 'g'), '')
          //   result.content = result.content.replace(new RegExp('\n', 'g'), '')
          //   // result.content = result.content.replace("\n", '')
          //   console.log(result.content)
          // }
          // --------
          that.setData({
            topicdetails: result,
            getTopicFlag: true,
            isFirstGetTopicFlag: false
          })
          if (res.data) {

          }
          if (that.data.isPullRefresh) {
            wx.showToast({
              title: '刷新成功',
              icon: 'success'
            })
            that.data.isPullRefresh = false
            wx.stopPullDownRefresh()
          }

        } else {
          that.setData({
            pageNo: this.data.pageNo - 1,
            getTopicFlag: true
          })
          if (that.data.isPullRefresh) {
            wx.showToast({
              title: '刷新失败',
              icon: 'none'
            })
            that.data.isPullRefresh = false
            wx.stopPullDownRefresh()
          }
        }
      } else {
        wx.showToast({
          title: '获取信息失败',
          icon: "none"
        })
      }
    }, err => {
      if (that.data.isPullRefresh) {
        wx.showToast({
          title: '刷新失败',
          icon: 'none'
        })
        that.data.isPullRefresh = false
        wx.stopPullDownRefresh()
      } else {
        that.setData({
          topicLists: res.data.result.records,
          getTopicFlag: false
        })
      }
    })
  },
  //点击topic图片放大预览
  clickTopicImage(event) {
    var imageList = []
    for (var itemImage in event.currentTarget.dataset.imagelist) {
      imageList.push(this.data.UPLOAD_IMAGE + event.currentTarget.dataset.imagelist[itemImage].topicImage + app.globalData.ARTWORK)
    }
    wx.previewImage({
      urls: imageList, //需要预览的图片http链接列表，注意是数组
      current: this.data.UPLOAD_IMAGE + event.currentTarget.id + app.globalData.ARTWORK, // 当前显示图片的http链接，默认是第一个
      success: function (res) { },
      fail: function (res) { },
      complete: function (res) { },
    })
  },



  //mark:收藏
  clickStar(e) {
    var that = this
    //若果没有收藏  收藏并展示动画
    if (!this.data.topicdetails.userIsStar) {
      var topicListTem = this.data.topicdetails
      topicListTem.userIsStar = true
      topicListTem.executeStarAnimation = true
      topicListTem.starCount = topicListTem.starCount + 1
      this.setData({
        topicdetails: topicListTem,
      })
      let url = that.data.SHAREHOSTURL + '/bbs/bbsUserStar/wise/mini/clickStar?topicId=' + topicListTem.id + '&isStar=' + true
      app.wxRequest('post', url, '').then(res => {
        console.log(that.data.topicIndex)
        if (that.data.topicIndex != -1) {
          app.globalData.needModUserIsStar = [true, that.data.topicIndex]
        }
        wx.showToast({
          title: '收藏成功',
          icon: "none"
        })
      }, err => {
        wx.showToast({
          title: '收藏失败',
          icon: "none"
        })
      })
    } else {
      var topicListTem = this.data.topicdetails
      topicListTem.userIsStar = false
      topicListTem.executeStarAnimation = false
      topicListTem.starCount = topicListTem.starCount - 1
      this.setData({
        topicdetails: topicListTem,
      })
      let url = that.data.SHAREHOSTURL + '/bbs/bbsUserStar/wise/mini/clickStar?topicId=' + topicListTem.id + '&isStar=' + false
      app.wxRequest('post', url, '').then(res => {
        if (that.data.topicIndex != -1) {
          app.globalData.needModUserIsStar = [false, that.data.topicIndex]
        }
        wx.showToast({
          title: '取消收藏成功',
          icon: "none"
        })
      }, err => {
        wx.showToast({
          title: '取消收藏失败',
          icon: "none"
        })
      })
    }
  },
  // mark: 贴子点赞
  clickPraise(e) {
    console.log(e)
    const topicId = e.currentTarget.dataset.topicid
    var that = this
    // 可以通过 wx.getSetting 先查询一下用户是否授权了 "scope.record" 这个 scope
    if (app.globalData.HASUSERINFO) {
      //若果没有点赞  点赞并展示动画
      if (!that.data.topicdetails.userIsPraise) {
        var topicListTem = that.data.topicdetails
        topicListTem.userIsPraise = true
        topicListTem.exeCuteAnimation = true
        topicListTem.praiseCount = topicListTem.praiseCount + 1
        that.setData({
          topicdetails: topicListTem,
        })
        let url = that.data.SHAREHOSTURL + '/bbs/bbsUserPraise/wise/mini/clickPraise?topicId=' + topicId + '&isPraise=' + true
        app.wxRequest('post', url, '').then(res => {

        }, err => {

        })
      } else {
        var topicListTem = that.data.topicdetails
        topicListTem.userIsPraise = false
        topicListTem.exeCuteAnimation = false
        topicListTem.praiseCount = topicListTem.praiseCount - 1
        that.setData({
          topicdetails: topicListTem,
        })
        let url = that.data.SHAREHOSTURL + '/bbs/bbsUserPraise/wise/mini/clickPraise?topicId=' + topicId + '&isPraise=' + false
        app.wxRequest('post', url, '').then(res => {

        }, err => {
          wx.showToast({
            title: '失败，请稍后再试。',
            icon: 'none'
          })
        })
      }
    } else {
      app.getUserProfile()
    }
  },
  // mark: 举报
  clickWarn(e) {
    var that = this
    var topicListTem = this.data.topicdetails
    topicListTem.executeWarnAnimation = false
    this.setData({
      topicdetails: topicListTem,
    })
    topicListTem.executeWarnAnimation = true
    this.setData({
      topicdetails: topicListTem,
    })
    console.log(e)
    wx.showModal({
      confirmColor: '#000000',
      title: '举报',
      content: "您确认要举报这条信息吗？",
      showCancel: true,
      success(res) {
        if (res.confirm) {
          console.log(that.data.topicdetails)
          var bbsInform = {}
          bbsInform.topicId = that.data.topicdetails.id
          bbsInform.type = 1 //默认
          bbsInform.beInformUsername = that.data.topicdetails.createBy
          bbsInform.informUsername = wx.getStorageSync('SYSUSER').username
          let url = that.data.SHAREHOSTURL + '/bbs/bbsInform/wise/mini/informTopic'
          app.wxRequest('post', url, bbsInform).then(res => {
            wx.showToast({
              title: res.data.message,
              icon: "none"
            })
          }, err => {

          })
        } else if (res.cancel) {

        }
      }
    })
  },


  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    var that = this
    let shareUrl = '/pages/index/index?topicId=' + that.data.topicId
    let shareTitle = ""
    let topicdetails = that.data.topicdetails
    if (null == topicdetails.title || "" == topicdetails.title) {
      shareTitle = topicdetails.title
    } else {
      shareTitle = topicdetails.content
    }

    let shareImageUrl = ''
    if (that.data.topicdetails.bbsTopicImageList.length != 0 && null != that.data.topicdetails.bbsTopicImageList[0].topicImage && "" != that.data.topicdetails.bbsTopicImageList[0].topicImage) {
      shareImageUrl = that.data.UPLOAD_IMAGE + that.data.topicdetails.bbsTopicImageList[0].topicImage + that.data.ARTWORKNOWATER
    }
    console.log(shareUrl)
    // /pages/index/index?topicId=1350415381262532609
    return {
      title: shareTitle,
      path: shareUrl,
      imageUrl: shareImageUrl
    }
  },
  // mark:分享朋友圈
  onShareTimeline: function () {
    var that = this

    let shareTitle = ""
    let topicdetails = that.data.topicdetails
    if (undefined == topicdetails.title || null == topicdetails.title || "" == topicdetails.title) {
      shareTitle = topicdetails.content
    } else {
      shareTitle = topicdetails.title
    }
    // console.log(shareTitle)
    let imageList = that.data.topicdetails.bbsTopicImageList
    let imageUrl = ""
    if (0 == imageList.length || null == imageList[0].topicImage) {
      imageUrl = ''
    } else {
      imageUrl = that.data.UPLOAD_IMAGE + imageList[0].topicImage + that.data.ARTWORKNOWATER
    }
    // console.log(imageUrl)
    return {
      title: shareTitle,
      query: "topicId=" + that.data.topicdetails.id + "&regionCode=" + that.data.topicdetails.regionCode,
      imageUrl: imageUrl
      // imageUrl: that.data.topicdetails.bbsTopicImageList.length == 0 ? '' : that.data.UPLOAD_IMAGE + that.data.topicdetails.bbsTopicImageList[0].topicImage + that.data.ARTWORKNOWATER
    }
  },
  // mark: 复制联系方式
  copyContact(e) {
    console.log(e)
    wx.setClipboardData({
      data: e.currentTarget.dataset.contact,
      success(res) {
        // wx.getClipboardData({
        //   success(res) {
        console.log(res.data) // data
        //   }
        // })
      }
    })
  },
  clickMore() {
    let actionTopic = this.data.topicdetails
    let SYSUSER = wx.getStorageSync('SYSUSER')
    let actionGroups = []
    if (actionTopic.userIsStar) {
      let actionGroupItem = {
        text: '取消收藏',
        value: 0
      }
      actionGroups.push(actionGroupItem)
    } else {
      let actionGroupItem = {
        text: '收藏',
        value: 0
      }
      actionGroups.push(actionGroupItem)
    }

    // 举报按钮展示
    if (actionTopic.topicType == "0") {
      if (SYSUSER.username != actionTopic.createBy) {
        let actionGroupItem = {
          text: '举报',
          value: 1
        }
        actionGroups.push(actionGroupItem)
      }
    }

    this.setData({
      actionTopic: actionTopic,
      showActionsheet: true,
      actionGroups: actionGroups
    })
  },
  // mark: 点击action，触发相应方法
  clickAction(e) {
    console.log(e)
    var that = this
    if (e.detail.index == 0) {
      this.clickStar(e.detail.topic)
    } else if (e.detail.index == 1) {
      this.clickWarn()
    }
    this.setData({
      showActionsheet: false
    })
  },

  goLink(e) {
    let bbsTopicLinkItem = e.currentTarget.dataset.bbstopiclinkitem
    console.log(bbsTopicLinkItem)

    if (bbsTopicLinkItem.linkType == 0) { //跳转贴子
      wx.navigateTo({
        url: './topicdetails?topicId=' + bbsTopicLinkItem.linkUrl,
      })
    } else if (bbsTopicLinkItem.linkType == 1) { //跳转功能页
      wx.navigateTo({
        url: bbsTopicLinkItem.linkUrl + bbsTopicLinkItem.parameter,
      })
    }
  },
  goUserInfo(e) {
    console.log(e)
    wx.navigateTo({
      url: '/pages/components/mine/userinfo/userinfo?username=' + e.currentTarget.dataset.username,
    })
  }

});