// pages/rooms/room.js
let { WsServer } = require("../../config/config.js")
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo:null,
    src:'',
    play:false,
    video:{
      src:""
    },
    process:0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.data.userInfo = wx.getStorageSync("user");
    console.log(this.data.userInfo)
    this.data.name = options.name
    WsServer.sendText({
      target:"JoinRoom",
      data:{
        nickName:this.data.userInfo.nickName,
        avatarUrl:this.data.userInfo.avatarUrl,
        joinRoom:options.name
      }
    })
    WsServer.onMessage(res=>{
      console.log(res)
      this.runMap(res)
    })
  },
  oNprocess(e){
    if(e.detail.currentTime<e.detail.duration){
      this.data.process = e.detail.currentTime;
    }else{
      this.data.process = 0
    }
    

    
  },
  videoEnd(){
    WsServer.sendText({
      target: "SetVideo",
      data: {
        name: this.data.name,
        video: {
          play: false,
          process:"0",
          src:this.data.src
        }
      }
    })
  },
  runMap({ target, data }) {
    let map = {
      users: () => {
        this.setData({
          users: data
        })
      },
      SetVideo:()=>{
        let video= wx.createVideoContext('asyncVideo');
        if(data.src){
          this.setData({
            src:data.src 
          })
        }

        if(data.process&&!this.data.play){
          video.seek(parseFloat(data.process))
        }
       
        if(data.play){
          
          this.setData({
            play:true
          })
          video.play()
        }else{
          video.pause();
          this.setData({
            play: false
          })
        }
      }
    }

    map[target]&&map[target]()
  },
  play(){
   if(!this.data.play){
     WsServer.sendText({
       target: "SetVideo",
       data: {
         name: this.data.name,
         video: {
           play: true,
           process:this.data.process,
           src:this.data.src
         }
       }
     })
   }
  },
  pause(){
    if(this.data.play){
      WsServer.sendText({
        target: "SetVideo",
        data: {
          name: this.data.name,
          video: {
            play: false,
            process:this.data.process,
            src:this.data.src
          }
        }
      })
    }
  },
  setUrl(e){
    WsServer.sendText({
      target:"SetVideo",
      data:{
        name:this.data.name,
        video:{
          src: e.detail.value,
          play:false
        }
      }
    })
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
    wx.onSocketOpen((result) => {
      console.log(resilt)
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
    WsServer.sendText({
      target: "ExitRoom",
      data: {
        nickName: this.data.userInfo.nickName,
        joinRoom: this.data.name
      }
    })
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