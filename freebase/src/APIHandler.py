'''
Created on Feb 4, 2014

@author: Harriet
'''
import json
import urllib

api_key = "AIzaSyDQbIuXIm0TZVRS1yrd4lNwZvSRDLbg-_c"
service_url = 'https://www.googleapis.com/freebase/v1/mqlread'
query = [{'id': None, 'name': None, 'type': '/music/album', 'artist': [], 'genre': []}]
params = {
        'query': json.dumps(query),
        'key': api_key,
        'limit': 200,
        'filter': '(all with:artist with:genre)'
}
cursor = ''
url = service_url + '?' + urllib.urlencode(params) + '&cursor'
response = json.loads(urllib.urlopen(url).read())
cursor = response['cursor']
datafile = open('rawdata.txt', 'w')

while(cursor):
    print response
    for result in response['result']:
        try:
            str_list = []
            str_list.append(result['name'] + '*')
            if len(result['artist']) == 0 or len(result['genre']) == 0:
                break
            for artist in result['artist']:
                str_list.append(artist + ',')
            str_list.append('*')
            for genre in result['genre']:
                str_list.append(',' + genre)
            str_list.append('\n')
            line = ''.join(str_list)
            datafile.write(line)
        except UnicodeEncodeError:
            print 'UnicodeEncodeError'
        except TypeError:
            print 'TypeError'
    if cursor:        
        url = service_url + '?' + urllib.urlencode(params) + '&cursor=' + cursor
        response = json.loads(urllib.urlopen(url).read())
        cursor = response['cursor']