import sys
import datetime
import calendar

TIME_WEIGHT = 1
DAY_WEIGHT = 1
GAME_WEIGHT = 1
CATEGORY_WEIGHT = 1
CITY_WEIGHT = 1
AGE_WEIGHT = 1
NUM_PLAYERS_WEIGHT = 1

dayOfWeekEnum = {v: k for k,v in enumerate(calendar.month_abbr)}
ballGamesList = ['Soccer', 'Football', 'Basketball', 'Foosball', 'Tennis']
ballGamesEnum = {v: k for k,v in enumerate(ballGamesList)}
BoardGamesList = ['Overwatch', 'League Of Legends', 'Call Of Duty', 'Fortnite']
BoardGamesEnum = {v: k for k,v in enumerate(BoardGamesList)}
VideoGamesList = ['Catan', 'Clue', 'Risk', 'Talisman', 'Monopoly']
VideoGamesEnum = {v: k for k,v in enumerate(VideoGamesList)}
workoutList = ['Spinning', 'Zumba', 'Bicycling', 'Yoga']
workoutEnum = {v: k for k,v in enumerate(workoutList)}
citiesList = ['Haifa', 'Tel-Aviv', 'Jerusalem', 'Netanya', 'Beer-sheva', 'Eilat', 'Nahariya', 'Qiryat-shemona']
citiesEnum = {v: k for k,v in enumerate(citiesList)}


def timeToValue(time):
    list = time.split(':')
    return (int(float(list[0]))*4) + (int(float(list[1]))/15)

def timeValueToString(time):
    hour = (time - (time % 4))/4
    min = (time % 4)*15
    return '{:02d}'.format(hour) + ':' + '{:02d}'.format(min)

def dayOfWeekToValue(year,month,day):
    return datetime.date(int(year), int(dayOfWeekEnum[month]), int(day)).weekday()


def gameToValue(game):
    return 'TODO'

def categoryToValue(category):
    return 'TODO'

def cityToValue(city):
    return 'TODO'

#age
#num players

def AddPostToHistogram(users,year,month,day,time,post,userid):
    users[userid]['timeList'].append(timeToValue(time))
    users[userid]['dayOfWeekList'].append(dayOfWeekToValue(year,month,day))



def main(argv):

##import DB from file
    # import json
    # with open("gameplan-1312c-export.json", 'r') as f:
    #     firebase = json.load(f)
    # users = firebase["users"]

##import DB from firebase
    from firebase import firebase
    firebase = firebase.FirebaseApplication('https://gameplan-1312c.firebaseio.com/', None)
    users = firebase.get('users', None)


    usersRawData = {}

    for userId,userData in users.items():

        usersRawData[userId] = {}
        usersRawData[userId]['timeList'] = []
        usersRawData[userId]['dayOfWeekList'] = []
        usersRawData[userId]['ageList'] = []
        usersRawData[userId]['numPlayersList'] = []

        attendingPosts = userData["attending"]
        for year,yearData in attendingPosts.items():
            for month,monthData in yearData.items():
                for day,dayData in monthData.items():
                    for time,timeData in dayData.items():
                        for postKey,postData in timeData.items():
                            print ("*post*")
                            print(postKey)
                            AddPostToHistogram(usersRawData,year,month,day,time,postData,userId)
    print (usersRawData)













if __name__ == '__main__':
	main(sys.argv)





