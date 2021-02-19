// pages/home/home.js
let app = getApp();
let {WsServer} = require("../../config/config.js")
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    online:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let user = wx.getStorageSync("user");
    if (!user) {
      wx.navigateTo({
        url: '/pages/index/index',
      })
    } else {
      this.setData({
        userInfo: user
      })
      //连接服务器
      WsServer.connect(user);
      
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    WsServer.onMessage(res => {
      console.log(res)
      this.runMap(res)
    })
    WsServer.sendText({
      target:"Rooms"
    })
  },
  runMap({target,data}){
    let map = {
      online:()=>{
        this.setData({
          online:data
        })
      },
      rooms:()=>{
        this.setData({
          rooms: data
        })
        console.log(this.data.rooms)
      }
    }

    map[target]&&map[target]()
  },
  createPlay(){
    wx.sendSocketMessage({
      data: JSON.stringify(
        {
          target: "CreateRoom",
          data: {
            nickName: this.data.userInfo.nickName,
            avatarUrl: this.data.userInfo.avatarUrl
          }
        }
      ),
    })
  },
  addRoom(e){
    console.log(e)
    wx.navigateTo({
      url: '/pages/rooms/room?name=' + e.currentTarget.dataset.name,
    })
  },
  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})