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

Currently, there are 2 headers available:
- The logo header will display the same logo on all pages
- The Context Aware header will display a logo corresponding to the context, e.g. on a job it displays 
  the job status or the job weather. On Folders the folder icon or the folder weather, on computers the computer state.
  And on many manage pages the corresponding symbol. If you find a missing mapping please open an issue. You can also
  create a properties file with a custom mapping between the class and the symbol to replace. This feature allows to overwrite
  the weather with your own symbols.

Users can choose to use a different header than what is globally defined.
This includes showing the original header. They can also just overwrite the coloring

## The logo
The logo can be turned off, you can use the default Jenkins logo, use a png or jpeg image, an SVG icon.
Ideally you use an SVG as they can be made working nicely with the dark theme.
You can also choose a symbol from ionicons (see [Design Library](https://weekly.ci.jenkins.io/design-library/Symbols/))
how to specify a symbol (e.g. the screenshots below use `symbol-jenkins`)

## The logo text
By default, the text `Jenkins` is displayed. You can choose any text you like or make it empty to display nothing.
Logo and logo text form a hyperlink that gets you back to the Jenkins root page (similar to the original header).

## The title
Optionally an additional title can be shown after a separator. This can contain html (sanitized with owasp), e.g. to 
include a link to some other site. Css already takes care to make the link properly styled.

## Application links and favorites
The plugin allows to configure additional links that are accessible via a button to the left of the logo. Users can define their personal
links in their settings.

When the [Favorite Plugin](https://plugins.jenkins.io/favorite) is installed, your personal favorites will
be added as well to this menu.

![App Links](/docs/pics/app-links.png)<br/>

## System Messages
One or more system messages can be shown between the header and the breadcrumb bar. This allows to notify users about important
things related to the instance, e.g. a planned update of Jenkins, a downtime due to hardware replacement or an ongoing 
incident. You can include html (sanitized with owasp) in the message to apply some simple styling or include a link
with more details.<br/>
System messages can have an expiration time to automatically remove them. They can be dismissed on a per-user basis.

![System Message](/docs/pics/system-message.png)<br/>

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


### Screenshots
![Red](/docs/pics/red-header.png)<br/>
![Blue](/docs/pics/blue-header.png)<br/>
![Green](/docs/pics/green-header.png)<br/>
![Grey Dark](/docs/pics/grey-header-dark-theme.png)

#### Dark Theme
![Red Dark Theme](/docs/pics/red-header-dark-theme.png)

#### Gradient
![Gradient](/docs/pics/gradient.png)

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

