**Branch: feature/views** metalcon/views/pages

# Description
All files in this folder will serve as templates for the `Freemarker` template engine and will be processed by the middleware.

# Pages
Pages consist of the header area where all the site default elements like search, filters and common navigation links reside.
On the main part there will be displayed context spefic informations on the left side and additional tab previews on the right side.

| Name        | Description                                       | Further ressources |
| ---         | ---                                               | ---                |
| `band.html` | This page lists all information to a certain band |                    |
| `user.html` | This page lists all information to a single user  |                    |

# Tab previews
The tab previews are small sections on the right side which show some preview information in each section. For example, the fans tab preview on a band page will show a small selection (maybe 8-10 pictures) of the fans of the band.
There are various tab previews with different content types (text, images, event dates,...) within and the following list will give an overview:

| Name                     | Description                                                                                               | Further ressources |
| ---                      | ---                                                                                                       | ---                |
| `bandsTabPreview.html`   | Shows a small selection of bands which are linked/liked by the currently viewed entity.                   |                    |
| `eventsTabPreview.html`  | Shows a small selection of events the current entity is attending to or referenced by.                    |                    |
| `PhotosTabPreview.html`  | Shows a small selection of thumbnails.                                                                    |                    |
| `RecordsTabPreview.html` | Shows a small selection of records of bands or from bands who attending to an event or something similar. |                    |
| `ReviewsTabPreview.html` | Shows some small excerp of an record review.                                                              |                    |
| `TracksTabPreview.html`  | Displays a media control item to play the most recent song of the band.                                   |                    |
| `UsersTabPreview.html`   | Shows a small selection of profile pictures of users which are linked with the currently viewed entity.   |                    |
| `VenuesTabPreview.html`  | Shows a small list of venues where events happen or bands will play.                                      |                    |

