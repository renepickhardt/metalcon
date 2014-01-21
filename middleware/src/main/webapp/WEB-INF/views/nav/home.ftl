<#ftl encoding="UTF-8" strict_syntax=true>
<#import "/spring.ftl" as spring>
<#import "/metalcon.ftl" as mtl>
<@mtl.html>
  <@mtl.head title="Home - Metalcon"/>
  <@mtl.body>
    <h1>Metalcon Middleware</h1>
    <p>Welcome to the Metalcon Middleware!</p>
    <p>Find information under <a href="https://github.com/renepickhardt/metalcon/wiki/componentMiddleware">componentMiddleware (Github Wiki)</a>.</p>
    <h2>URLs</h2>
    <p>URLs are specified under <a href="https://github.com/renepickhardt/metalcon/wiki/UrlMapping">UrlMapping (Github Wiki)</a>.</p>
    <p>There currently only exists a few dummy entities. They can be acces from
    the following URLs:</p>
    <ul>
      <li>
        <strong>Band</strong>
        <ul>
          <li><a href="<@spring.url "/music/Ensiferum"/>"><code>/music/Ensiferum</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum-12"/>"><code>/music/Ensiferum-12</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum-22"/>"><code>/music/Ensiferum-22</code></a></li>
        </ul>
      </li>
      <li>
        <strong>City</strong>
        <ul>
          <li><a href="<@spring.url "/city/Koblenz"/>"><code>/city/Koblenz</code></a></li>
          <li><a href="<@spring.url "/city/Koblenz-17"/>"><code>/city/Koblenz-17</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Event</strong>
        <ul>
          <li><a href="<@spring.url "/event/Wacken"/>"><code>/event/Wacken</code></a></li>
          <li><a href="<@spring.url "/event/Wacken-16"/>"><code>/event/Wacken-16</code></a></li>
          <li><a href="<@spring.url "/event/2014-08-31-Wacken"/>"><code>/event/2014-08-31-Wacken</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Genre</strong>
        <ul>
          <li><a href="<@spring.url "/genre/Black-Metal"/>"><code>/genre/Black-Metal</code></a></li>
          <li><a href="<@spring.url "/genre/Black-Metal-18"/>"><code>/genre/Black-Metal-18</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Instrument</strong>
        <ul>
          <li><a href="<@spring.url "/instrument/Guitar"/>"><code>/instrument/Guitar</code></a></li>
          <li><a href="<@spring.url "/instrument/Guitar-19"/>"><code>/instrument/Guitar-19</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Record</strong>
        <ul>
          <li><a href="<@spring.url "/music/Ensiferum/Victory-Songs"/>"><code>/music/Ensiferum/Victory-Songs</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum/Victory-Songs-13"/>"><code>/music/Ensiferum/Victory-Songs-13</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum/2007-Victory-Songs"/>"><code>/music/Ensiferum/2007-Victory-Songs</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Tour</strong>
        <ul>
          <li><a href="<@spring.url "/tour/Heidenfest"/>"><code>/tour/Heidenfest</code></a></li>
          <li><a href="<@spring.url "/tour/Heidenfest-10"/>"><code>/tour/Heidenfest-10</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Track</strong>
        <ul>
          <li><a href="<@spring.url "/music/Ensiferum/Victory-Songs/Ahti"/>"><code>/music/Ensiferum/Victory-Songs/Ahti</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum/Victory-Songs/Ahti-14"/>"><code>/music/Ensiferum/Victory-Songs/Ahti-14</code></a></li>
          <li><a href="<@spring.url "/music/Ensiferum/Victory-Songs/04-Ahti"/>"><code>/music/Ensiferum/Victory-Songs/04-Ahti</code></a></li>
        </ul>
      </li>
      <li>
        <strong>User</strong>
        <ul>
          <li><a href="<@spring.url "/user/James-Hetfield"/>"><code>/user/James-Hetfield</code></a></li>
          <li><a href="<@spring.url "/user/James-Hetfield-11"/>"><code>/user/James-Hetfield-11</code></a></li>
        </ul>
      </li>
      <li>
        <strong>Venue</strong>
        <ul>
          <li><a href="<@spring.url "/venue/Druckluftkammer"/>"><code>/venue/Druckluftkammer</code></a></li>
          <li><a href="<@spring.url "/venue/Druckluftkammer-17"/>"><code>/venue/Druckluftkammer-17</code></a></li>
          <li><a href="<@spring.url "/venue/Druckluftkammer-Koblenz"/>"><code>/venue/Druckluftkammer-Koblenz</code></a></li>
        </ul>
      </li>
    </ul>
    <h2>Tabs</h2>
    <p>Currently if no other tab is specified, the default tab for all entities
    is the NewsfeedTab.</p>
    <p>You can append a tab mapping to any of the URLs. (Note that some
    entity types dont support a tap mapping so they will 404).</p>
    <ul>
      <li><code>/about</code></li>
      <li><code>/bands</code></li>
      <li><code>/events</code></li>
      <li><code>/newsfeed</code></li>
      <li><code>/photos</code></li>
      <li><code>/recommendations</code></li>
      <li><code>/records</code></li>
      <li><code>/reviews</code></li>
      <li><code>/tracks</code></li>
      <li><code>/users</code></li>
      <li><code>/venues</code></li>
    </ul>
    <h2>Model</h2>
    <p>You can append <code>.json</code> to any URL to view the model as
    json data instead of the view.</p>
  </@mtl.body>
</@mtl.html>