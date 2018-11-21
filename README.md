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


## Example 

```
let text = 'hello world';
let imageURL = 'http://img.yaplog.jp/img/15/pc/k/u/r/kuro-memo/1/1357.jpg';

TWComposer.compse(text, imageURL).then(res => {
  // TODO;
}).catch(error => {
  // TODO;
});
```