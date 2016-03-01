# EqWaves - Simple visual equaliser for Android

#### This project aims to provide pretty cool equaliser for any Android audio project. Made in [Yalantis] (https://yalantis.com/?utm_source=github)

#### [How We Created EqWaves] (TODO: link to article)

<img src="https://dl.dropboxusercontent.com/content_link/GiNWvdW7dfHmMdvAMU9N5BBVpfCgGZx1nj04KssrLnNOnN1eG5M09rLkzR5h1oT9/file" alt="example" style="width:720;height:400">

# Usage

*For a working implementation, please have a look at the Sample Project - sample*

<a href="https://play.google.com/store/apps/details?id=com.yalantis.eqwaves&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="185" height="60"/></a>

1. Include the library as local library project.

    ``` compile 'com.yalantis:eqwaves:1.0.0' ```
    
2. Initialize Eqwave object with params regarding to your sound

    ````java
    mEqwaves = new Eqwaves(glSurfaceView, getResources().getColor(R.color.background),
                    RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
    ```

3. To update Equaliser call updateView method with chunk of sound data to proceed

	````java
   byte[] buffer = new byte[bufferSize];
   //here we put some sound data to the buffer
   mEqwaves.updateView(buffer);
    ```
# Compatibility
  
  * Library - Android ICS 4.0+
  * Sample - Android ICS 4.0+
  
# Changelog

### Version: 1.0

  * Initial Build

### Let us know!

We’d be really happy if you sent us links to your projects where you use our component. Just send an email to github@yalantis.com And do let us know if you have any questions or suggestion regarding the library. 

## License

    Copyright 2016, Yalantis

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.









