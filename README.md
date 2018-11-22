# cordova-plugin-twitter-composer

## Screen Shot

![2018-11-22 00 49 56](https://user-images.githubusercontent.com/13277036/48852461-bf2afe80-edf0-11e8-9bed-ed28e91ee0d7.png)

## Instalation

```
$ cordova plugin add cordova-plugin-twitter-composer
```

or 

add below code on config.xml
```
<plugin name="cordova-plugin-twitter-composer" spec="0.0.1">
    <variable name="TWITTERCONSUMERKEY" value="xxxx" />
    <variable name="TWITTERCONSUMERSECRET" value="xxxx" />
</plugin>
```

## Cocoapod

this plugin use cocoapod. please install cocoapods

```
$ gem install cocoapods
$ pod setup
```


## Example 

```
let text = 'This is a cat';
let imageURL = 'http://img.yaplog.jp/img/15/pc/k/u/r/kuro-memo/1/1357.jpg';


TWComposer.compse(text, imageURL).then(res => {
  // success or canceling tweet
  res.status // ['success', 'cancel']

  // you can setting the action after tweeting
}).catch(error => {
  // faild
  res.status // ['failed']
});
```