# cordova-plugin-twitter-composer

## Screen Shot

iOS

![2018-11-22 00 49 56](https://user-images.githubusercontent.com/13277036/48852461-bf2afe80-edf0-11e8-9bed-ed28e91ee0d7.png)

Android

![screenshot_20181128-212416](https://user-images.githubusercontent.com/13277036/49152078-0ff0a900-f355-11e8-8b03-7525c254a503.png)



## Instalation

```
$ cordova plugin add cordova-plugin-twitter-composer
```

or 

add below code on config.xml
```
<plugin name="cordova-plugin-twitter-composer" spec="0.0.9">
    <variable name="TWITTERCONSUMERKEY" value="xxxx" />
    <variable name="TWITTERCONSUMERSECRET" value="xxxx" />
</plugin>
```

## Cocoapod

This plugin use cocoapods. please install cocoapods

```
$ gem install cocoapods
$ pod setup
```


## Example 

```
let text = 'This is a cat';
let imageURL = 'http://img.yaplog.jp/img/15/pc/k/u/r/kuro-memo/1/1357.jpg';

// login and compose
TWComposer.compse(text, imageURL).then(res => {
  // success or canceling tweet
  res.status // ['success', 'cancel']

  // you can setting the action after tweeting
}).catch(error => {
  // faild
  res.status // ['failed']
});

// logout method
TWComposer.logout().then(() => {
  // after logout
});
```