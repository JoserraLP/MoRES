SERVER_API_URL = "http://192.168.1.61:8080"
PLACES_API_URL = "https://places.demo.api.here.com/places/v1/categories/places/?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg#"
LOCATION_API_URL = "http://ip-api.com/json/"

# Radius for the different roles  
RADIUS_ROLE = {
    'admin': 10000000000,
    'politician_country': 10000000000,
    'politician_admin_area': 1000000,
    'politician_locality': 10000
}

DESCRIPTIONS = {
    "People_Moving_HIGH" : "People is actually moving a lot",
    "People_Moving_HIGH_NIGHT" : "People is actually moving a lot by night",
    "Patrol_Moving_HIGH" : "There are too many patrols moving",
    "Patrol_Moving_LOW" : "There are not enough patrols moving",
}