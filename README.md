Android Google Location API Wrapper
======

A simple wrapper that allows you to subscribe to your own location.

Include
-----

Include jcenter in your repo list:
```
    
    buildscript {
        repositories {
            jcenter()
        }
    }
```
Add to your dependencies:

```

    compile 'com.shredder:location:0.0.+'
    
```

Usage
-----
```java

        GoogleLocationProvider googleLocationProvider = new GoogleLocationProvider(getActivity());
        googleLocationProvider.addLocationListener(new GoogleLocationProvider.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location result) {
                //access the various properties of your location
                result.getLatitude();
                result.getLongitude();
                result.getTime();
            }
        });
        googleLocationProvider.setConfig(new LocationConfig() {
            @Override
            public LocationAccuracy getAccuracy() {
                //Depending on your requirements, choose how accurate you want to be. 
                //Higher accuracy = higher battery consumption.

                return LocationAccuracy.Lowest;
                //          return LocationAccuracy.Low;
                //          return LocationAccuracy.High;
                //          return LocationAccuracy.Highest; //default
            }
        });

    
```

