## Backlog

- NOTE: Each time an item below is completed, it should be written down as a feature if appropriate.
- [Setting] denotes that the feature and a setting/toggle for the feature must be implemented.

- NOTE: Each time a dependency is added to the project, it must also be added to LicensesList.kt

### Feature List
- Works offline, including past searches

### Alpha
- Set up Room database
    - Custom lists
        - movie_lists table
               * id: Long
                * name: String
                * order: Int
            + Pre-populated data (do this in RoomDb)
                * watchlist (order: 1)
                * watched (order: 2) - Add each movie to this when it has been marked as watched 
        - movie_lists_join table
            + Columns
                * movie_id: Long
                * list_id: Long
        - tv_show_lists table
            + Columns
                * id: Long
                * name: String
                * order: Int
            + Pre-populated data (do this in RoomDb)
                * watchlist (order: 1)
                * watched - only add to this when every episode is complete
                * history (order: 2) - Add each episode to this when it has been marked as watched 
        - tv_show_lists_join table
            + Columns
                * show_id: Long
                * content: Long
                * contentType: Enum (tv show, episode or season)
    - tv show
        + tv show - add flag 
        + season - when an episode is marked as 'watched', update the season 'watchCount'
        + episode - when an episode is marked as 'watched', update the episode 'watched' flag
- The 'up next'/'watchlist' screen should have a button so that it is 'one click' to mark the next episode as 'watched'. So you should see the episode you're currently on, be able to click 'watched' and the next episode should come up. Also add 'undo' snackbar for this action for accidental clicks.
- [Setting] Shortcut to add to watchlist on grid screens like a long or double tap?
    + When user clicks save, check database to see if movie details are in database already. If not, then call movie/detail network call first and then perform the save.
- Show fallback data and offline message if the network requests fail but we still have database data.
- Change error layout so that whole screen is clickable to retry instead of just a button
- Make genre chips clickable - goes to discover filter
- Caching
    - Look at TMDB 'changes' api calls - could incorporate this into caching
- Add an 'Add to list' button on the detail screen which brings up the users lists. Use PopupMenu.
- Implement Person detail screen
- Implement Shows Screen
    + Watchlist
    + Watch history/Watched
- Implement Movies Screen
    + Watchlist
    + Watch history/Watched
- Implement remaining sections on detail screen such as 'seasons'
- TV show/movie lists
    + Have option to sort the list items, e.g.
        * Recently watched
        * Newest episode
        * Alphabetical
        * Total episodes
        * Episodes left to watch
- Add genres tab to the discover screen tabs to easily look at genre (as well as in the advanced filter)
- [Setting] Add setting to show all runtimes for episodes instead of just one (show one by default).
- Add ability to follow a person as well as a tv show and movie so you can go through and watch someone's filmography.
- Advanced search/filters. E.g. by genre (using Discover API)
- Figure out when to invalidate data, e.g. stored movies, tv shows and people.
    + When to refresh the data (e.g. when someone goes to the movie detail screen, the getMovie() call should go to the network instead of the database if X time has passed or something)
    + When to delete the data (e.g. old searches)
- WorkManager
    + Pre-load and cache the data needed for discover to work instantly (use WorkManager to periodically fetch data and also fetch it on first app run)
    + Decide when to invalidate/delete some of the past searches. E.g. only keep the 50 most recent searches.
    + Fetch and store a new config every 3 days (call the ConfigRepository refreshConfig() function)
- [Setting] Update the grid/list pages to show an icon to show which content you have already watched without clicking into the detail screen
- Implement the list toggle mode xml layout
- Cut down all data models I don't use to decrease memory footprint. For example on the list/grid screens, we probably don't need a lot of the attributes other than the name and the image urls for the content. Then when we navigate to the detail screen, we get everything
- Refresh config from TMDB every 3 days as per documentation
- Display TMDB attribution in the app
- Add 'About' screen to the settings screen
    + Logo
    + App name
    + Add 'View/rate the app on Google play' button
    + Add social links
    + TMDB attribution
    + Link to UserReport to suggest improvements
- Figure out the best tabs to show in discover
- Go through TODOs
- Abstract the Dao's and Retrofit services behind a 'DataSource' interface for testing
- Make sure that menu item navigation is implemented on all screens (e.g. for settings)
- Ensure that all ViewHolders use 'LayoutContainer' from kotlin android extensions on ViewHolders for view caching
- Unit tests
- Integration tests
- Run lint checks and analyse project

### Alpha (Design/Final Touches)
- Watch videos on app performance and tune everything
- Fonts and capitalisation
- Create a launcher icon - also an adaptive icon
- Polish and look for consistency
- Consistent loading and error visuals
- Update iconography, e.g. in the bottom navigation
- Ensure that all layouts are shallow and fast - use ConstraintLayout where needed
- Check rotation and horizontal layouts look ok
- Make sure animateLayoutChanges is working in the RecyclerViews (also check it animates when we change toggle view modes in all layouts)

### Alpha Build/Release
- Test the release APK, especially for missing ProGuard rules etc.
- Add privacy policy to app and play store
- Use the release checklist to make sure everything is done
    + https://developer.android.com/distribute/best-practices/launch/launch-checklist
- Turn on Proguard/R8 and configure rules for all libraries
    + Go through our Gradle dependencies and implement ProGuard configurations (such as keeping certain classes) for all libraries. Running ProGuard without this configuration usually breaks classes used e.g. in Dagger 2 or Retrofit, so we should check how to configure it for all dependencies as well as our own code.
    + Enable R8 and compare APK size before and after minify.
- Add deobfuscation files to developer console for bug reporting
- Check for Google play policy compliance
    + Read the guidelines on compliance and the different rules
- Go through the play store listing and optimise everything
    + Make sure to say things like 'this app does not allow you to watch movies or tv shows' and give TMDB attribution etc.
    + Make sure there is no copyrighted content in the listing

### Beta
- Shared element transitions
- Touch surface ripple animations
- Add transitions for fragments, e.g. slide up when entering
- Add button to mark 'all episodes before this one' as watched
- Don't show a '0.00' rating for content - show 'n/a' or something instead
- [Setting] Light, dark and black themes
    + Use DayNight theme
- [Setting] Add setting to show/hide rating on grid view so it doesn't take up space over thumbnail - Default to hidden
- [Setting] Add setting to change region and release times. After implementing the setting, the API calls should reflect this change.
- [Setting] Ability to reorder the movie/tv/person details screen sections in the recyclerview from the settings.
- Access a gallery of posters and backdrops on the detail screen (these are on TMDB as a separate request)
- Statistics screen
    + Additional work to make sure that whenever anything is done in the app (such as the user marking a movie as 'watched'), the statistics are calculated and updated in the database.

### 1.0 Release
- New name and package maybe? - UpNext is already taken. Find and replace all instances of 'UpNext' from the project
- Accessibility - make sure that Talkback works properly and explore the accessibility attributes in XML. Also add all contentDescriptions on ImageViews and dynamically set contentDescriptions for images for movies etc.
- Reminders/notifications
- Custom lists and lists based on filters. Could be used for stuff like dvd collection etc.
- Backup/import - manual and auto with Google Drive
- Watch history screen (probably accessed via statistics screen).
- Create layouts specifically for tablets
- Ability to add missing movies (e.g. random videos that aren't in the database) or add links to youtube videos etc to watch later.
- Add a launch/splash screen? (using theme switch)

### Extra Features/When I Have Spare Time (Dependant on demand)
- Trakt syncing
- TMDB list syncing
- Settings panel on Android Q for when internet is offline - add button to error layout to bring it up
- Ability to add own rating (is this stored on the app, TMDB or Trakt?)
- Deep linking and sharing tv shows, movies and people
- Add home screen widgets (e.g. upcoming, watchlist)
- Calendar screen for upcoming shows and movies
- Add support for slices and actions
- Palette API?
- Ability to save searches
- Recommendations
- Ability to download or share posters and backdrops
- Links on the detail screen to TMDB, IMDB, Trakt, Amazon etc.
- TMDB discussions
- Rotten Tomatoes, IMDB, Metacritic and Trakt.tv ratings
    + Use OMDB to get some ratings
- Links/plugins to external websites to watch etc.
- [Setting] Ability to disable movies or tv shows if you only watch one content type. This would remove the bottom tab and also filter any search results
- [Setting] Ability to reorder the bottom tabs (e.g. if you use movies more) and/or set the default tab when app opens
- 'top/featured' result on the search screen - different viewholder (full width) for the first item

### When I have Spare Time (Project/Tech Debt)
- Modularise the codebase


