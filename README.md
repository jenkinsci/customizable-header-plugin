Customizable Header Plugin
==========

## About the plugin
This plugin allows to customize the Jenkins header.

You can adjust the logo, the text next to the logo, add another title and easily change colors.  
Search box, alerts (for admin) and user links are the same as in the original header.

Currently, there are 2 headers available:
- The logo header will display the same logo on all pages
- The Context Aware header will display a logo corresponding to the context, e.g. on a job it displays 
  the job status or the job weather. On Folders the folder icon or the folder weather, on computers the computer state.
  And on many manage pages the corresponding symbol. If you find a missing mapping please open an issue. You can also
  create a properties file with a custom mapping between the class and the symbol to replace. This feature allows to overwrite
  the weather with your own symbols.

## The weather symbols
To demonstrate the custom weather symbols download the svgs from `docs/svgs` to `userContent/svgs` in your 
`JENKINS_HOME`, put the following into a properties file in your `JENKINS_HOME` and configure the additional mappings 
to it. 
```
icon-health-00to19=file-userContent/svgs/mood-sick.svg
icon-health-20to39=file-userContent/svgs/mood-sad.svg
icon-health-40to59=file-userContent/svgs/mood-empty.svg
icon-health-60to79=file-userContent/svgs/mood-smile.svg
icon-health-80plus=file-userContent/svgs/mood-happy.svg
```

## The logo
The logo can be turned off, you can use the default Jenkins logo, use a png or jpeg image, an SVG icon.
Ideally you use an SVG as they can be made working nicely with the dark theme.
You can also choose a symbol from ionicons (see [Design Library](https://weekly.ci.jenkins.io/design-library/Symbols/))
how to specify a symbol (e.g. the screenshots below use `symbol-jenkins`)

## The logo text
By default, the text `Jenkins` is displayed. You can choose any text you like or make it empty to display nothing.
Logo and logo text form a hyperlink that gets you back to the Jenkins root page (similar to the original header).

## The title
Optionally an additional title can be shown after a separator. This can contain html (sanitized with owasp), e.g. to include a link to some
other site. Css already takes care to make the link properly styled.

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

To adjust the links inside the alerts and users apply your styling to the class `.page-header__hyperlinks a `.

### Screenshots
![Red](/docs/pics/red-header.png)<br/>
![Blue](/docs/pics/blue-header.png)<br/>
![Green](/docs/pics/green-header.png)<br/>
![Grey Dark](/docs/pics/grey-header-dark-theme.png)

#### Dark Theme
![Red Dark Theme](/docs/pics/red-header-dark-theme.png)



### Context Aware Header Screenshots
#### Job Status
![Weather-4](docs/pics/weather-4.png)

#### Weather
![Weather-3](docs/pics/weather-3.png)

#### Custom Weather
![Weather-1](docs/pics/weather-1.png)<br/>
![Weather-2](docs/pics/weather-2.png)

#### Manage something
![Manage](docs/pics/manage-jenkins.png)<br/>
![Security](docs/pics/configure-security.png)

