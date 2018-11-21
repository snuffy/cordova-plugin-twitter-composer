# cordova-plugin-twitter-composer

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


## HOW TO USE ?

```
let text = 'hello world';
let imageURL = 'http://img.yaplog.jp/img/15/pc/k/u/r/kuro-memo/1/1357.jpg';

TWComposer.compse(text, imageURL).then(res => {
  // TODO;
}).catch(error => {
  // TODO;
});
```