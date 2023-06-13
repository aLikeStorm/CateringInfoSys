## 1. 首页
vue初始化完成，获取根据本机ip获取本地城市code
页面初始完成 发送axios请求
> 请求方式 GET
> 
> url "/restaurants/{cityCode}"
> 
> 得到这个城市评论数最多的几个餐饮点信息

搜索餐饮点
选择城市、餐饮点类型、餐饮点名
> 请求方式 GET
> 
> url "/restaurants/{cityCode}/{typeCode}/{餐饮点名}"
> 
> 查询得到餐饮点列表

点击餐饮点div跳转到指定餐饮点vue
到指定餐饮点

## 指定餐饮点页面
餐饮点页面初始化完成发送请求
> 请求方式 GET
> 
> url "restaurants/getDetail/{餐饮点ID}"
> 
> 返回餐饮点的信息

获取餐饮点的优惠券
> GET
> 
> "/coupon/getRestCoupon/{餐饮点ID}"
> 
> 返回优惠卷列表

点击获取指定优惠卷
> POST
> 
> "/coupon/snappedCoupon"
> 
> 参数 {rid:餐饮点ID,cid:优惠卷ID: }
> 
> 返回是否抢购成功

获取餐饮点评论
> GET
> 
> "/comments/getRestComments/{餐饮点ID}"

 为评论点赞或取消点赞
> PATCH
> 
> "/comments/likeComment/{评论ID}"

为评论上传照片
> POST
> 
> "/file/uploadImages"
> 文件名images
>返回文件imageList 前端去指定文件夹中取出图片显示到页面


提交评论
> POST 
>
> "/comments/addComment"
> 
> 携带参数 {comment: ,rid:店铺id,imageList:[上传图片文件名的列表]}


## 个人主页
> get
> 
> "/user"
> 
> 返回个人账号 头像图片名

获取我的评论
> get
> 
> "/comments/self"
> 
> 返回个人评论列表

删除指定评论
> "/comments/delete/{评论id}"

查询个人优惠卷
> "/coupon/getMyCoupons"
> 
> 返回优惠卷

编辑个人信息
> POST 
> 
> "user/update"
> {user对象}

登陆
账号登陆
>POST  
> 账号登陆
> "/user/login/account
> 
> {user对象，用户名，密码}
>
邮箱登陆
> post
> 
> "/user/login/email"
>  {邮箱}
> 
> post
> "/user/login/code"
> 
> {email,code}
> 返回token 登陆成功

注册
> POST
> "/user/register"
> {username,email,password}

登出
>Get
> "/user/logout"




