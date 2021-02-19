
let WsServer = "ws://121.36.100.85:8080/ws/"
let nickName = wx.getStorageSync('nickName')
let initEventHandle = function() {
  wx.onSocketOpen(() => {
    console.log('WebSocket连接打开')
    wx.showToast({
      title: '服务器连接成功',
    })
  })
  wx.onSocketError(function (res) {
    console.log('WebSocket连接打开失败')
    
  })
  wx.onSocketClose(function (res) {
    console.log('WebSocket 已关闭！')
    wx.showToast({
      title: '服务器断开连接',
    })
    wx.connectSocket({
      url: WsServer+encodeURIComponent(nickName),
      header: { 'content-type': 'application/json' },
      success: function (res) {
        console.log(res)
        console.log('信道连接重新成功~')
      },
      fail: function () {
        console.log('信道连接重新失败~')
      }
    })
  })
}

function sendText(data={}){
  wx.sendSocketMessage({
    data: JSON.stringify(data),
  })
}

function connect({nickName}){
  wx.setStorageSync('nickName', nickName)
  wx.connectSocket({
    url: WsServer+encodeURIComponent(nickName),
    header: { 'content-type': 'application/json' },
    success: function (res) {
      console.log('信道连接成功~')
      initEventHandle()
    },
    fail: function () {
      console.log('信道连接失败~')
    }
  })
}

function onMessage(callback){
  wx.onSocketMessage(function(res){
    callback(JSON.parse(res.data))
  })
}
module.exports = {
  WsServer:{
    connect:connect,
    onMessage:onMessage,
    sendText:sendText
  }
}
