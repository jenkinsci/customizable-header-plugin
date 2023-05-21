Customizable Header Plugin
==========

## About the plugin
This plugin allows to customize the Jenkins header.

The header is visually divided into 2 parts. 
- On the left there is a logo followed by a text. As the original header this is a link to Jenkins root. 
This part uses the jenkins theme, thus colors change together with the selected theme.
- After a separator (just some simple clip-path with a polygon) the title is shown followed by the search box, alerts and user links.

Logo, text, title and colors can be easily changed without the need to write your own css.
In case fine-tuning is required (e.g. to change the padding between logo and text) you can include your own css file. 

## The logo
The logo can be turned off, you can use the default Jenkins logo, use a png or jpeg image, an SVG icon.
Ideally you use an SVG as they can be made working nicely with the dark theme.
You can also choose a symbol from ionicons (see [Design Library](https://weekly.ci.jenkins.io/design-library/Symbols/))
how to specify a symbol (e.g. the screenshots below use `symbol-jenkins`)

## The logo text
By default, the text `Jenkins` is displayed. You can choose any text you like or make it empty to display nothing.

## The title
Optionally an additional title can be shown after a separator.

## CSS classes assigned to the different elements
To style the header beyond changing colors you can specify your own css file.
Following code snippet shows the basic layout of the header and the relevant classes and ids used in the plugin.
```
<header class="page-header custom-header__page">
    <div class="custom-header">
        <a class ="custom-header__link">
            <img class="custom-header__logo" or <svg class="custom_header__logo"
            <div class="custom-header__text>
              Jenkins
            </div>
        </a>
    </div>
    <div class="custom-header__title">
      Title
    </div>
    
    seachbox
    alerts
    user
</header>
```


### Samples
![Red](/docs/pics/red-header.png)
![Blue](/docs/pics/blue-header.png)
![Green](/docs/pics/green-header.png)
![Grey Dark](/docs/pics/grey-header-dark-theme.png)
![Red Dark Theme](/docs/pics/red-header-dark-theme.png)

