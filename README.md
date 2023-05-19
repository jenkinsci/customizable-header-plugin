Customizable Header Plugin
==========

## About the plugin
This plugin allows to customize the Jenkins header.

You can adjust the logo, the text next to the logo and add another title. Colors can be easily adjusted by providing css file. 
Search box, alerts (for admin) and user links are the same as in the original header. 

## The logo
The logo can be turned off, you can use the default Jenkins logo, use a png or jpeg image, an SVG icon.
Ideally you use an SVG as they can be made working nicely with the dark theme.
You can also choose a symbol from ionicons (see [Design Library](https://weekly.ci.jenkins.io/design-library/Symbols/))
how to specify a symbol (e.g. the screenshots below use `symbol-jenkins`)

## The logo text
By default the text `Jenkins` is displayed. You can choose any text you like or make it empty to display nothing.
Logo and logo text form a hyperlink that gets you back to the Jenkins root page (similar to the original header).

## The title
Optionally an additional title can be shown after a separator.

## CSS classes assigned to the different elements
To style the header you can specify your own css file.
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
        <div class="custom-header__title">
          Title
        </div>
    </div>
    
    seachbox
    alerts
    user
</header>
```

To adjust the links inside the alerts and users apply your styling to the class `.page-header__hyperlinks a `.

### Samples
Click the images to view the CSS files

[![Red](/docs/pics/red-header.png)](/docs/samples/red.css)
[![Blue](/docs/pics/blue-header.png)](/docs/samples/blue.css)
[![Green](/docs/pics/green-header.png)](/docs/samples/green.css)
[![Grey Dark](/docs/pics/grey-header-dark-theme.png)](/docs/samples/grey.css)

#### Dark Theme
![Red Dark Theme](/docs/pics/red-header-dark-theme.png)

