#!/usr/bin/python3

import getpass
import requests
import json
#from pprint import pprint

username = input("%-- >> Username : ")
password = getpass.getpass("%-- >> Password : ")
srv_url = input("%-- >> Ariane server url (like http://serverFQDN:6969/) : ")

# CREATE REQUESTS SESSION
s = requests.Session()
s.auth = (username, password)

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications')
r.status_code
#200

#pprint(r.json())
#{'applications': [{'applicationColorCode': '6092c4',
#                   'applicationCompanyID': 1,
#                   'applicationDescription': 'Multicast bus for business applications',
#                   'applicationID': 1,
#                   'applicationName': 'Tibco RendezVous',
#                   'applicationOSInstancesID': [2, 3, 4, 5],
#                   'applicationShortName': 'TibcoRV',
#                   'applicationTeamID': 2,
#                   'applicationVersion': 9},
#                  {'applicationColorCode': '5e647a',
#                   'applicationCompanyID': 2,
#                   'applicationDescription': 'Linux hypervisor for virtualisation',
#                   'applicationID': 2,
#                   'applicationName': 'QEMU-KVM',
#                   'applicationOSInstancesID': [1],
#                   'applicationShortName': 'QEMU-KVM',
#                   'applicationTeamID': 1,
#                   'applicationVersion': 1},
#                  {'applicationColorCode': 'e3a164',
#                   'applicationCompanyID': 3,
#                   'applicationDescription': 'a fake app currently',
#                   'applicationID': 3,
#                   'applicationName': 'APP',
#                   'applicationOSInstancesID': [],
#                   'applicationShortName': 'APP',
#                   'applicationTeamID': 3,
#                   'applicationVersion': 4},
#                  {'applicationColorCode': '852e48',
#                   'applicationCompanyID': 3,
#                   'applicationDescription': 'a fake app currently',
#                   'applicationID': 4,
#                   'applicationName': 'BPP',
#                   'applicationOSInstancesID': [],
#                   'applicationShortName': 'BPP',
#                   'applicationTeamID': 4,
#                   'applicationVersion': 3},
#                  {'applicationColorCode': '158c6a',
#                   'applicationCompanyID': 3,
#                   'applicationDescription': 'a fake app currently',
#                   'applicationID': 5,
#                   'applicationName': 'CPP',
#                   'applicationOSInstancesID': [],
#                   'applicationShortName': 'CPP',
#                   'applicationTeamID': 5,
#                   'applicationVersion': 11},
#                  {'applicationColorCode': '5544c2',
#                   'applicationCompanyID': 24,
#                   'applicationDescription': 'AKKA',
#                   'applicationID': 10,
#                   'applicationName': 'AKKA',
#                   'applicationOSInstancesID': [],
#                   'applicationShortName': 'AKKA',
#                   'applicationTeamID': 9,
#                   'applicationVersion': 4}]}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/1')
#r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': '6092c4',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'Multicast bus for business applications',
# 'applicationID': 1,
# 'applicationName': 'Tibco RendezVous',
# 'applicationOSInstancesID': [2, 3, 4, 5],
# 'applicationShortName': 'TibcoRV',
# 'applicationTeamID': 2,
# 'applicationVersion': 9}

applicationParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params=applicationParams)
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': '6092c4',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'Multicast bus for business applications',
# 'applicationID': 1,
# 'applicationName': 'Tibco RendezVous',
# 'applicationOSInstancesID': [2, 3, 4, 5],
# 'applicationShortName': 'TibcoRV',
# 'applicationTeamID': 2,
# 'applicationVersion': 9}

applicationParams = {'name': "Tibco Rendez Vous"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params=applicationParams)
r.status_code
#404

applicationParams = {'name': "Tibco RendezVous"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params=applicationParams)
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': '6092c4',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'Multicast bus for business applications',
# 'applicationID': 1,
# 'applicationName': 'Tibco RendezVous',
# 'applicationOSInstancesID': [2, 3, 4, 5],
# 'applicationShortName': 'TibcoRV',
# 'applicationTeamID': 2,
# 'applicationVersion': 9}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

applicationParams = {'name': 'DPP', 'shortName': 'DPP', 'description': 'A fake app', 'colorCode': 'ffffff'}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/create', params=applicationParams)
r.status_code
#200
# pprint(r.json())
#{'applicationColorCode': 'ffffff',
# 'applicationCompanyID': 0,
# 'applicationDescription': 'A fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 0,
# 'applicationVersion': 0}
dppID = r.json().get('applicationID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/name', params={'id': dppID, 'name': 'dPP'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with name dPP'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/name', params={'id': dppID, 'name': 'DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/shortName', params={'id': dppID, 'shortName': 'dPP'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with short name dPP'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/shortName', params={'id': dppID, 'shortName': 'DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/description', params={'id': dppID, 'description': 'another fake app'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with description another fake app'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/colorCode', params={'id': dppID, 'colorCode': 'ddffddff'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with color code ddffddff'

payload = '{"applicationName":"fake App name", "applicationShortName":"fake app", "applicationColorCode": "dddddd", "applicationDescription":"This is fake app"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
fakeID = r.json().get('applicationID')
#200
# pprint(r.json())
#{'applicationColorCode': 'dddddd',
# 'applicationCompanyID': -1,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'fake App name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}

payload = '{"applicationID": '+ str(fakeID) +',"applicationName": "New Fake app name"}'
print(payload)
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'dddddd',
# 'applicationCompanyID': -1,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}

payload = '{"applicationID": '+ str(fakeID) +', "applicationShortName": "New Fake app"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'dddddd',
# 'applicationCompanyID': -1,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}

payload = '{"applicationID": '+ str(fakeID) +', "applicationColorCode": "de28de"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'de28de',
# 'applicationCompanyID': -1,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}

payload = '{"applicationID": '+ str(fakeID) +', "applicationDescription": "updated description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'de28de',
# 'applicationCompanyID': -1,
# 'applicationDescription': 'updated description',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies')
r.status_code
#200
#pprint(r.json())
#{'companies': [{'companyApplicationsID': [1],
#                'companyDescription': 'Tibco',
#                'companyID': 1,
#                'companyName': 'Tibco',
#                'companyOSTypesID': [],
#                'companyVersion': 0},
#               {'companyApplicationsID': [2],
#                'companyDescription': 'Red Hat Inc.',
#                'companyID': 2,
#                'companyName': 'Red Hat',
#                'companyOSTypesID': [1],
#                'companyVersion': 0},
#               {'companyApplicationsID': [3, 4, 5],
#                'companyDescription': 'The world is sound',
#                'companyID': 3,
#                'companyName': 'Spectral',
#                'companyOSTypesID': [],
#                'companyVersion': 1},
#               {'companyApplicationsID': [10],
#                'companyDescription': 'The Scala Company',
#                'companyID': 24,
#                'companyName': 'TypeSafe',
#                'companyOSTypesID': [],
#                'companyVersion': 0}]}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/1')
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

companyParams = {'id': "1"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params=companyParams)
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

companyParams = {'name': "Tibco"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params=companyParams)
r.status_code
tibcoCmp = r.json().get('companyID')
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/applications/add', params={'id': tibcoCmp, 'applicationID': dppID})
r.status_code
#200
#r.text
#'Company 1 has been successfully updated by adding application 11'

companyParams = {'name': "Tibco"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params=companyParams)
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1, 11],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get/', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ffffff',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'A fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 0,
# 'applicationVersion': 3}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/applications/delete', params={'id': tibcoCmp, 'applicationID': dppID})
r.status_code
#200
#r.text
#'Company 1 has been successfully updated by deleting application 11'

companyParams = {'name': "Tibco"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params=companyParams)
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ffffff',
# 'applicationCompanyID': 0,
# 'applicationDescription': 'A fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 0,
# 'applicationVersion': 4}

companyParams = {'name': "Spectral"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params=companyParams)
r.status_code
spectralCmp = r.json().get('companyID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/company', params={'id': dppID, 'companyID': spectralCmp})
r.text
#'Application 11 has been successfully updated with company 3'
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 0,
# 'applicationVersion': 11}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params={'id': spectralCmp})
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [3, 4, 5, 11],
# 'companyDescription': 'The world is sound',
# 'companyID': 3,
# 'companyName': 'Spectral',
# 'companyOSTypesID': [],
# 'companyVersion': 1}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/create', params={'name': 'Oracle', 'description': 'the devil company'})
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [],
# 'companyDescription': 'the devil company',
# 'companyID': 25,
# 'companyName': 'Oracle',
# 'companyOSTypesID': [],
# 'companyVersion': 0}
oracleCmp = r.json().get('companyID')

payload = '{"applicationID": '+ str(fakeID) +', "applicationCompanyID": '+ str(spectralCmp) +'}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'de28de',
# 'applicationCompanyID': 25,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': -1,
# 'applicationVersion': -1}


r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/name', params={'id': oracleCmp, 'name': 'Oracle Corp'})
r.status_code
#200
r.text
#'Company 25 has been successfully updated with name Oracle Corp'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/description', params={'id': oracleCmp, 'description': 'The devil company'})
r.status_code
#200
r.text
#'Company 25 has been successfully updated with description The devil company'





r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments')
r.status_code
#200
#pprint(r.json())
#{'environments': [{'environmentDescription': 'Development environment',
#                   'environmentID': 1,
#                   'environmentName': 'DEV',
#                   'environmentOSInstancesID': [1, 2, 3, 4, 5],
#                   'environmentVersion': 13}]}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/1')
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

envParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params=envParams)
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

envParams = {'name': "DEV"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params=envParams)
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/create', params={'name': 'HOM', 'description': 'Homologation environment'})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Homologation environment',
# 'environmentID': 2,
# 'environmentName': 'HOM',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}
qaEnv = r.json().get('environmentID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/name', params={'id': qaEnv, 'name': 'QA'})
r.status_code
#200
r.text
#'Environment 2 has been successfully updated with name QA'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/description', params={'id': qaEnv, 'description': 'QA environment'})
r.status_code
#200
r.text
#'Environment 2 has been successfully updated with name QA'





r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams')
r.status_code
#200
#pprint(r.json())
#{'teams': [{'teamApplicationsID': [2],
#            'teamColorCode': '054d31',
#            'teamDescription': 'Unix server support team',
#            'teamID': 1,
#            'teamName': 'SRV UNIX',
#            'teamOSInstancesID': [1, 2, 3, 4, 5],
#            'teamVersion': 7},
#           {'teamApplicationsID': [1],
#            'teamColorCode': '11301f',
#            'teamDescription': 'Middleware bus support team',
#            'teamID': 2,
#            'teamName': 'MDW BUS',
#            'teamOSInstancesID': [2, 3, 4, 5],
#            'teamVersion': 8},
#           {'teamApplicationsID': [3],
#            'teamColorCode': '71ab90',
#            'teamDescription': 'Dev. APP team',
#            'teamID': 3,
#            'teamName': 'DEV APP',
#            'teamOSInstancesID': [],
#            'teamVersion': 0},
#           {'teamApplicationsID': [4],
#            'teamColorCode': 'ad853b',
#            'teamDescription': 'Dev. BPP team',
#            'teamID': 4,
#            'teamName': 'DEV BPP',
#            'teamOSInstancesID': [],
#            'teamVersion': 0},
#           {'teamApplicationsID': [5],
#            'teamColorCode': 'b799cf',
#            'teamDescription': 'Dev. CPP team',
#            'teamID': 5,
#            'teamName': 'DEV CPP',
#            'teamOSInstancesID': [],
#            'teamVersion': 1},
#           {'teamApplicationsID': [10],
#            'teamColorCode': '946fde',
#            'teamDescription': 'MDW AKKA TEAM',
#            'teamID': 9,
#            'teamName': 'MDW AKKA',
#            'teamOSInstancesID': [],
#            'teamVersion': 4}]}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/1')
r.status_code
#200
#>>> pprint(r.json())
#{'teamApplicationsID': [2],
# 'teamColorCode': '054d31',
# 'teamDescription': 'Unix server support team',
# 'teamID': 1,
# 'teamName': 'SRV UNIX',
# 'teamOSInstancesID': [1, 2, 3, 4, 5],
# 'teamVersion': 7}

teamParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params=teamParams)
r.status_code
#200
#>>> pprint(r.json())
#{'teamApplicationsID': [2],
# 'teamColorCode': '054d31',
# 'teamDescription': 'Unix server support team',
# 'teamID': 1,
# 'teamName': 'SRV UNIX',
# 'teamOSInstancesID': [1, 2, 3, 4, 5],
# 'teamVersion': 7}

teamParams = {'name': "SRV UNIX"}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params=teamParams)
r.status_code
#200
#>>> pprint(r.json())
#{'teamApplicationsID': [2],
# 'teamColorCode': '054d31',
# 'teamDescription': 'Unix server support team',
# 'teamID': 1,
# 'teamName': 'SRV UNIX',
# 'teamOSInstancesID': [1, 2, 3, 4, 5],
# 'teamVersion': 7}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create', params={'name': 'DEV DPP', 'description': 'Dev. DPP team', 'colorCode': '1f45de'})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'DPP dev team',
# 'teamID': 10,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}
dppTeamID = r.json().get('teamID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create', params={'name': 'DEV DPP2', 'description': 'Dev. DPP team2', 'colorCode': '1f44de'})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '1f44de',
# 'teamDescription': 'DPP dev team2',
# 'teamID': 11,
# 'teamName': 'DEV DPP2',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}
dppTeamID2 = r.json().get('teamID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/name', params={'id': dppTeamID, 'name': 'DEV dpp'})
r.status_code
#200
#r = s.get(srvurl + 'ariane/rest/directories/common/organisation/teams/get', params={'id':dppTeamID})
#pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'DPP dev team',
# 'teamID': 10,
# 'teamName': 'DEV dpp',
# 'teamOSInstancesID': [],
# 'teamVersion': 2}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/name', params={'id': dppTeamID, 'name': 'DEV DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/description', params={'id': dppTeamID, 'description': 'Dev. DPP team'})
r.status_code
#200
#r = s.get(srvurl + 'ariane/rest/directories/common/organisation/teams/get', params={'id':dppTeamID})
#pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 10,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 3}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/delete', params={'id': dppTeamID})
r.status_code
#200
r.text
#'Team 10 has been successfully removed'
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams')
#pprint(r.json())
#{'teams': [{'teamApplicationsID': [2],
#            'teamColorCode': '054d31',
#            'teamDescription': 'Unix server support team',
#            'teamID': 1,
#            'teamName': 'SRV UNIX',
#            'teamOSInstancesID': [1, 2, 3, 4, 5],
#            'teamVersion': 7},
#           {'teamApplicationsID': [1],
#            'teamColorCode': '11301f',
#            'teamDescription': 'Middleware bus support team',
#            'teamID': 2,
#            'teamName': 'MDW BUS',
#            'teamOSInstancesID': [2, 3, 4, 5],
#            'teamVersion': 8},
#           {'teamApplicationsID': [3],
#            'teamColorCode': '71ab90',
#            'teamDescription': 'Dev. APP team',
#            'teamID': 3,
#            'teamName': 'DEV APP',
#            'teamOSInstancesID': [],
#            'teamVersion': 0},
#           {'teamApplicationsID': [4],
#            'teamColorCode': 'ad853b',
#            'teamDescription': 'Dev. BPP team',
#            'teamID': 4,
#            'teamName': 'DEV BPP',
#            'teamOSInstancesID': [],
#            'teamVersion': 0},
#           {'teamApplicationsID': [5],
#            'teamColorCode': 'b799cf',
#            'teamDescription': 'Dev. CPP team',
#            'teamID': 5,
#            'teamName': 'DEV CPP',
#            'teamOSInstancesID': [],
#            'teamVersion': 1},
#           {'teamApplicationsID': [10],
#            'teamColorCode': '946fde',
#            'teamDescription': 'MDW AKKA TEAM',
#            'teamID': 9,
#            'teamName': 'MDW AKKA',
#            'teamOSInstancesID': [],
#            'teamVersion': 4}]}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create', params={'name': 'DEV DPP', 'description': 'Dev. DPP team', 'colorCode': '1f45de'})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}
dppTeamID = r.json().get('teamID')

payload = '{"applicationID": '+ str(fakeID) +', "applicationTeamID": '+ str(dppTeamID) +'}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'de28de',
# 'application/CompanyID': -1,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': 11,
# 'applicationVersion': -1}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/applications/add', params={'id': dppTeamID, 'applicationID': dppID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [11],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 11,
# 'applicationVersion': 12}





r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters')
r.status_code
#200
#pprint(r.json())
#{'datacenters': [{'datacenterAddress': '26 rue de Belfort',
#                  'datacenterCountry': 'France',
#                  'datacenterDescription': 'dekatonshIVr lab',
#                  'datacenterGPSLat': 48.895308,
#                  'datacenterGPSLng': 2.246621,
#                  'datacenterID': 1,
#                  'datacenterRoutingAreasID': [1],
#                  'datacenterName': 'My little paradise',
#                  'datacenterSubnetsID': [1, 2, 3],
#                  'datacenterTown': 'Courbevoie',
#                  'datacenterVersion': 33,
#                  'datacenterZipCode': 92400}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/1')
r.status_code
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterRoutingAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

dcParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params=dcParams)
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterRoutingAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

dcParams = {'name': 'My little paradise'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params=dcParams)
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterRoutingAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/create',
          params={'name': 'Somewhere in hell [DR]', 'address': "Devil's Island", 'zipCode': 666, 'town': "Devil's Island", 'country': 'France',
                  'gpsLatitude': 5.295366, 'gpsLongitude': -52.582179, 'description': 'A fantasy DR DC'})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': "Devil's Island",
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DR DC',
# 'datacenterGPSLat': 5.295366,
# 'datacenterGPSLng': -52.582179,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Somewhere in hell [DR]',
# 'datacenterSubnetsID': [],
# 'datacenterTown': "Devil's Island",
# 'datacenterVersion': 0,
# 'datacenterZipCode': 666}
devilDCID = r.json().get('datacenterID')

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/name', params={'id': devilDCID, 'name': 'Hell (DR)'})
r.status_code
#200
r.text
#'Datacenter 2 has been successfully updated with name Hell (DR)'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': "Devil's Island",
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DR DC',
# 'datacenterGPSLat': 5.295366,
# 'datacenterGPSLng': -52.582179,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': "Devil's Island",
# 'datacenterVersion': 1,
# 'datacenterZipCode': 666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/description', params={'id': devilDCID, 'description': 'A fantasy DC for DR'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': "Devil's Island",
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DC for DR',
# 'datacenterGPSLat': 5.295366,
# 'datacenterGPSLng': -52.582179,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': "Devil's Island",
# 'datacenterVersion': 2,
# 'datacenterZipCode': 666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/fullAddress', params={'id': devilDCID, 'address': 'dreyfus hole', 'zipCode': 666666, 'town': '666 Island', 'country': 'France'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': 'dreyfus hole',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DC for DR',
# 'datacenterGPSLat': 5.295366,
# 'datacenterGPSLng': -52.582179,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': '666 Island',
# 'datacenterVersion': 3,
# 'datacenterZipCode': 666666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/gpsCoord', params={'id': devilDCID, 'gpsLatitude': 5.295666, 'gpsLongitude': -52.582169})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': 'dreyfus hole',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DC for DR',
# 'datacenterGPSLat': 5.295666,
# 'datacenterGPSLng': -52.582169,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': '666 Island',
# 'datacenterVersion': 5,
# 'datacenterZipCode': 666666}




r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas')
r.status_code
#200
#pprint(r.json())
#{'routingAreas': [{'routingAreaDatacentersID': [1],
#                     'routingAreaDescription': 'lab01 routing area',
#                     'routingAreaID': 1,
#                     'routingAreaMulticast': 'NOLIMIT',
#                     'routingAreaName': 'angelsMind',
#                     'routingAreaSubnetsID': [1],
#                     'routingAreaType': 'LAN',
#                     'routingAreaVersion': 5}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/1')
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [1],
# 'routingAreaDescription': 'lab01 routing area',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'angelsMind',
# 'routingAreaSubnetsID': [1],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 5}

rareaParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params=rareaParams)
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [1],
# 'routingAreaDescription': 'lab01 routing area',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'angelsMind',
# 'routingAreaSubnetsID': [1],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 5}

rareaParams = {'name': "angelsMind"}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params=rareaParams)
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [1],
# 'routingAreaDescription': 'lab01 routing area',
# 'routingAreaID': 1,
# 'routingAreaMulticast': NOLIMIT,
# 'routingAreaName': 'angelsMind',
# 'routingAreaSubnetsID': [1],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 5}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create', params={'name': "devilsMindLAN", 'description': "666 mind", 'type': "LAN", 'multicast': "NOLIMIT"})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}
devilRareaID = r.json().get("routingAreaID")

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create', params={'name': "devilsMindMAN", 'description': "666 mind", 'type': "MAN", 'multicast': "TOTO"})
r.status_code
#400
#r.text
#'Invalid multicast flag. Correct multicast flags values are : [NONE, FILTERED, NOLIMIT]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create', params={'name': "devilsMindMAN", 'description': "666 mind", 'type': "MAN", 'multicast': "TOTO"})
r.status_code
#400
#r.text
#'Invalid type. Correct type values are : [LAN, MAN, WAN]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/routingareas/add', params={'id': devilDCID, 'routingareaID': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': 'dreyfus hole',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DC for DR',
# 'datacenterGPSLat': 5.295666,
# 'datacenterGPSLng': -52.582169,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [2],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': '666 Island',
# 'datacenterVersion': 6,
# 'datacenterZipCode': 666666}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [2],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/update/routingareas/delete', params={'id': devilDCID, 'routingareaID': devilRareaID})
r.status_code
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': 'dreyfus hole',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'A fantasy DC for DR',
# 'datacenterGPSLat': 5.295666,
# 'datacenterGPSLng': -52.582169,
# 'datacenterID': 2,
# 'datacenterRoutingAreasID': [],
# 'datacenterName': 'Hell (DR)',
# 'datacenterSubnetsID': [],
# 'datacenterTown': '666 Island',
# 'datacenterVersion': 6,
# 'datacenterZipCode': 666666}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/name', params={'id': devilRareaID, 'name': 'Mind of devil'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/description', params={'id': devilRareaID, 'description': 'just a crazy mind'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/datacenters/add', params={'id': devilRareaID, 'datacenterID': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [2],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/datacenters/delete', params={'id': devilRareaID, 'datacenterID': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaDatacentersID': [],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/datacenters/add', params={'id': devilRareaID, 'datacenterID': devilDCID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/type', params={'id': devilRareaID, 'type': 'MAN'})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/type', params={'id': devilRareaID, 'type': 'toto'})
r.status_code
#400
#r.text
#'Invalid type. Correct type values are : [LAN, MAN, WAN]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/multicast', params={'id': devilRareaID, 'multicast': 'FILTERED'})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/multicast', params={'id': devilRareaID, 'multicast': 'TOTO'})
r.status_code
#400
#r.text
#'Invalid multicast flag. Correct multicast flags values are : [NONE, FILTERED, NOLIMIT]'



r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets')
r.status_code
#200
#pprint(r.json())
#{'subnets': [{'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 LAN1',
#              'subnetID': 1,
#              'subnetIP': '192.168.33.0',
#              'subnetMask': '255.255.255.0',
#              'subnetRoutingAreaID': 1,
#              'subnetName': 'lab01.lan1',
#              'subnetOSInstancesID': [1, 2, 3],
#              'subnetType': 'LAN',
#              'subnetVersion': 23},
#             {'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 MAN',
#              'subnetID': 2,
#              'subnetIP': '192.168.34.0',
#              'subnetMask': '255.255.255.0',
#              'subnetRoutingAreaID': -1,
#              'subnetName': 'lab01.man',
#              'subnetOSInstancesID': [1, 4],
#              'subnetType': 'MAN',
#              'subnetVersion': 8},
#             {'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 WAN',
#              'subnetID': 3,
#              'subnetIP': '192.168.35.0',
#              'subnetMask': '255.255.255.0',
#              'subnetRoutingAreaID': -1,
#              'subnetName': 'lab01.wan',
#              'subnetOSInstancesID': [1, 5],
#              'subnetType': 'WAN',
#              'subnetVersion': 4}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/1')
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

subnetParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params=subnetParams)
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

subnetParams = {'name': "lab01.lan1"}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params=subnetParams)
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/create', params={'name': 'dummy.subnet', 'subnetIP': '123.123.48.0', 'subnetMask': '255.255.240.0',
                                                                                                    'routingArea': devilRareaID, 'description': 'a fake subnet'})
r.status_code
dummy_subnetID = r.json().get('subnetID')

#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'dummy subnet',
# 'subnetID': 5,
# 'subnetIP': '123.123.48.0',
# 'subnetMask': '255.255.240.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.subnet',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/create', params={'name': 'fake.subnet', 'subnetIP': '192.168.66.0', 'subnetMask': '255.255.255.0',
                                                                                                    'routingArea': devilRareaID, 'description': 'a fake subnet'})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.66.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.subnet',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}
fake_subnet_ID = r.json().get('subnetID')

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/name', params={'id': fake_subnet_ID, 'name': 'fake.lan'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.66.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/subnetip', params={'id': fake_subnet_ID, 'subnetIP': '192.168.69.69'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/subnetmask', params={'id': fake_subnet_ID, 'subnetMask': '255.255.255.255'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/description', params={'id': fake_subnet_ID, 'description': 'A fake subnet'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea', params={'id': fake_subnet_ID, 'routingareaID': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': 2,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'MAN',
# 'subnetVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#....


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea', params={'id': fake_subnet_ID, 'routingareaID': -1})
r.status_code
#404
#r.text
#'Routing Area -1 not found.'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea', params={'id': fake_subnet_ID, 'routingareaID': devilRareaID})
r.status_code
#200


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress')
r.status_code
#200
#pprint(r.json())
#["ipAddresses": {
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 5
# },{
#     "ipAddressID": 2,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.122",
#     "ipAddressFQDN": "Fake FQDN2"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 5
# }]

ipAddressParams = {'ipAddress': '123.123.48.123', 'fqdn':'Fake FQDN', 'networkSubnet': dummy_subnetID, 'osInstance':-1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/create', params=ipAddressParams)
r.status_code

fake_ipAddressID = r.json().get('ipAddressID')
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 5
# }

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/1')
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 5
#}

ipAddressParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'ipAddress': '123.123.48.123'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 5
#}

ipAddressParams = {'id': fake_ipAddressID, 'ipAddress': '123.123.48.119'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/ipAddress', params=ipAddressParams)
r.status_code
#200

ipAddressParams = {'id': fake_ipAddressID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.119",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'id': fake_ipAddressID, 'fqdn': 'Fake FQDN3'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/fqdn', params=ipAddressParams)
r.status_code
#200

ipAddressParams = {'id': fake_ipAddressID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.119",
#     "ipAddressFQDN": "Fake FQDN3"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'id': fake_ipAddressID, 'subnetID': fake_subnet_ID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/subnet', params=ipAddressParams)
r.status_code
#200

ipAddressParams = {'id': fake_ipAddressID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.119",
#     "ipAddressFQDN": "Fake FQDN3"
#     "ipAddressOSInstancesID": -1,
#     "ipAddressSubnetID": 2
#}


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes')
r.status_code
#200
#pprint(r.json())
#{'osTypes': [{'osTypeArchitecture': 'x86_64',
#              'osTypeCompanyID': 2,
#              'osTypeID': 1,
#              'osTypeName': 'Fedora 18',
#              'osTypeOSInstancesID': [1, 2, 3, 4, 5],
#              'osTypeVersion': 8}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/1')
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

ostParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params=ostParams)
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

ostParams = {'name': "Fedora 18"}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params=ostParams)
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/create', params={'name': 'Solaris', 'architecture': 'sparc-32'})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'sparc-32',
# 'osTypeCompanyID': -1,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}
solID = r.json().get('osTypeID')

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/name', params={'id': solID, 'name': 'Solaris OS'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'sparc-32',
# 'osTypeCompanyID': -1,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris OS',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/architecture', params={'id': solID, 'architecture': 'x86_64'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': -1,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris OS',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/company', params={'id': solID, 'companyID': oracleCmp})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 25,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris OS',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params={'id': oracleCmp})
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [],
# 'companyDescription': 'The devil company',
# 'companyID': 25,
# 'companyName': 'Oracle Corp',
# 'companyOSTypesID': [2],
# 'companyVersion': 2}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/ostypes/delete', params={'id': oracleCmp, 'ostypeID': solID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params={'id': oracleCmp})
r.status_code
#pprint(r.json())
#{'companyApplicationsID': [],
# 'companyDescription': 'The devil company',
# 'companyID': 25,
# 'companyName': 'Oracle Corp',
# 'companyOSTypesID': [],
# 'companyVersion': 2}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': -1,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris OS',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/ostypes/add', params={'id': oracleCmp, 'ostypeID': solID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params={'id': oracleCmp})
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [],
# 'companyDescription': 'The devil company',
# 'companyID': 25,
# 'companyName': 'Oracle Corp',
# 'companyOSTypesID': [2],
# 'companyVersion': 2}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 25,
# 'osTypeID': 2,
# 'osTypeName': 'Solaris OS',
# 'osTypeOSInstancesID': [],
# 'osTypeVersion': 0}








r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances')
r.status_code
#200
#pprint(r.json())
#{'osInstances': [{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
#                  'osInstanceApplicationsID': [2],
#                  'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
#                  'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5],
#                  'osInstanceEmbeddingOSInstanceID': -1,
#                  'osInstanceEnvironmentsID': [1],
#                  'osInstanceID': 1,
#                  'osInstanceName': 'hvirt.dekatonshIVr',
#                  'osInstanceOSTypeID': 1,
#                  'osInstanceSubnetsID': [1, 2, 3],
#                  'osInstanceTeamsID': [1],
#                  'osInstanceVersion': 4},
#                 {'osInstanceAdminGateURI': 'ssh://tibrvrdl03prd01.lab01.dev.dekatonshivr.echinopsii.net',
#                  'osInstanceApplicationsID': [1],
#                  'osInstanceDescription': 'Tibco RVRD OS Instance',
#                  'osInstanceEmbeddedOSInstancesID': [],
#                  'osInstanceEmbeddingOSInstanceID': 1,
#                  'osInstanceEnvironmentsID': [1],
#                  'osInstanceID': 2,
#                  'osInstanceName': 'tibrvrdl03prd01',
#                  'osInstanceOSTypeID': 1,
#                  'osInstanceSubnetsID': [1],
#                  'osInstanceTeamsID': [1, 2],
#                  'osInstanceVersion': 2},
#                 {'osInstanceAdminGateURI': 'ssh://tibrvrdl05prd01.lab01.dev.dekatonshivr.echinopsii.net',
#                  'osInstanceApplicationsID': [1],
#                  'osInstanceDescription': 'Tibco RVRD OS Instance',
#                  'osInstanceEmbeddedOSInstancesID': [],
#                  'osInstanceEmbeddingOSInstanceID': 1,
#                  'osInstanceEnvironmentsID': [1],
#                  'osInstanceID': 3,
#                  'osInstanceName': 'tibrvrdl05prd01',
#                  'osInstanceOSTypeID': 1,
#                  'osInstanceSubnetsID': [1],
#                  'osInstanceTeamsID': [1, 2],
#                  'osInstanceVersion': 0},
#                {'osInstanceAdminGateURI': 'ssh://tibrvrdmprd01.lab01.dev.dekatonshivr.echinopsii.net',
#                  'osInstanceApplicationsID': [1],
#                  'osInstanceDescription': 'Tibco RVRD OS Instance',
#                  'osInstanceEmbeddedOSInstancesID': [],
#                  'osInstanceEmbeddingOSInstanceID': 1,
#                  'osInstanceEnvironmentsID': [1],
#                  'osInstanceID': 4,
#                  'osInstanceName': 'tibrvrdmprd01',
#                  'osInstanceOSTypeID': 1,
#                  'osInstanceSubnetsID': [2],
#                  'osInstanceTeamsID': [1, 2],
#                  'osInstanceVersion': 0},
#                 {'osInstanceAdminGateURI': 'ssh://tibrvrdwprd01.lab01.dev.dekatonshivr.echinopsii.net',
#                  'osInstanceApplicationsID': [1],
#                  'osInstanceDescription': 'Tibco RVRD OS Instance',
#                  'osInstanceEmbeddedOSInstancesID': [],
#                  'osInstanceEmbeddingOSInstanceID': 1,
#                  'osInstanceEnvironmentsID': [1],
#                  'osInstanceID': 5,
#                  'osInstanceName': 'tibrvrdwprd01',
#                  'osInstanceOSTypeID': 1,
#                  'osInstanceSubnetsID': [3],
#                  'osInstanceTeamsID': [1, 2],
#                  'osInstanceVersion': 2}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/1')
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}

osiParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params=osiParams)
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}

osiParams = {'name': "hvirt.dekatonshIVr"}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params=osiParams)
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/create', params={'name': 'fakeOs', 'adminGateURI': 'ssh://fakeOs.fake.lan', 'description': 'a fake os'})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'a fake os',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
fakeOSID = r.json().get("osInstanceID")

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/create', params={'name': 'fakeOs2', 'adminGateURI': 'ssh://fakeOs.fake2.lan', 'description': 'a fake os2'})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs.fake2.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'a fake os2',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs2',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
fakeOSID2 = r.json().get("osInstanceID")

payload = '{"applicationID": '+ str(fakeID) +',"applicationOSInstancesID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
#200
# pprint(r.json())
#{'applicationColorCode': 'de28de',
# 'applicationCompanyID': 25,
# 'applicationDescription': 'This is fake app',
# 'applicationID': 12,
# 'applicationName': 'New Fake app name',
# 'applicationOSInstancesID': [1,2,3],
# 'applicationShortName': 'New Fake app',
# 'applicationTeamID': 11,
# 'applicationVersion': -1}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/name', params={'id': fakeOSID, 'name': 'fakeOs1'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'a fake os',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/description', params={'id': fakeOSID, 'description': 'A fake OS'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/admingateuri', params={'id': fakeOSID, 'adminGateURI': "ssh://fakeOs1.fake.lan"})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ostype', params={'id': fakeOSID, 'ostID': solID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddingOSInstance', params={'id': fakeOSID, 'osiID': 1})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': 1})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5, 7],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddedOSInstances/delete', params={'id': 1, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': 1})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddedOSInstances/add', params={'id': 1, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': 1})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://hvirt.lab01.dev.dekatonshivr.echinopsii.net',
# 'osInstanceApplicationsID': [2],
# 'osInstanceDescription': 'DekatonshIVr QEMU-KVM OS Instance',
# 'osInstanceEmbeddedOSInstancesID': [2, 3, 4, 5, 7],
# 'osInstanceEmbeddingOSInstanceID': -1,
# 'osInstanceEnvironmentsID': [1],
# 'osInstanceID': 1,
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/subnets/add', params={'id': fakeOSID, 'subnetID': fake_subnet_ID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [7],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/subnets/delete', params={'id': fakeOSID, 'subnetID': fake_subnet_ID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/add', params={'id': fake_subnet_ID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [7],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/delete', params={'id': fake_subnet_ID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/add', params={'id': fakeOSID, 'osiID': fakeOSID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/applications/add', params={'id': fakeOSID, 'applicationID': dppID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [7],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 11,
# 'applicationVersion': 14}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/applications/delete', params={'id': fakeOSID, 'applicationID': dppID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 11,
# 'applicationVersion': 14}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/add', params={'id': dppID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [7],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 11,
# 'applicationVersion': 14}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/delete', params={'id': dppID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#200
#pprint(r.json())
#{'applicationColorCode': 'ddffddff',
# 'applicationCompanyID': 3,
# 'applicationDescription': 'another fake app',
# 'applicationID': 11,
# 'applicationName': 'DPP',
# 'applicationOSInstancesID': [],
# 'applicationShortName': 'DPP',
# 'applicationTeamID': 11,
# 'applicationVersion': 14}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/add', params={'id': dppID, 'osiID': fakeOSID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/environments/add', params={'id': fakeOSID, 'environmentID': qaEnv})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params={'id': qaEnv})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'QA environment',
# 'environmentID': 2,
# 'environmentName': 'QA',
# 'environmentOSInstancesID': [7],
# 'environmentVersion': 3}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/environments/delete', params={'id': fakeOSID, 'environmentID': qaEnv})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params={'id': qaEnv})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'QA environment',
# 'environmentID': 2,
# 'environmentName': 'QA',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 3}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ipAddresses/add', params={'id': fakeOSID, 'ipAddressID': fake_ipAddressID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params={'id': fake_ipAddressID})
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstancesID": 1,
#     "ipAddressSubnetID": 5
# }

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ipAddresses/delete', params={'id': fakeOSID, 'ipAddressID': fake_ipAddressID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}


r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/add', params={'id': qaEnv, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params={'id': qaEnv})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'QA environment',
# 'environmentID': 2,
# 'environmentName': 'QA',
# 'environmentOSInstancesID': [7],
# 'environmentVersion': 3}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/delete', params={'id': qaEnv, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params={'id': qaEnv})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'QA environment',
# 'environmentID': 2,
# 'environmentName': 'QA',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 3}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/add', params={'id': qaEnv, 'osiID': fakeOSID})
r.status_code
#200


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/teams/add', params={'id': fakeOSID, 'teamID': dppTeamID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [11],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [11],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [7],
# 'teamVersion': 1}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/teams/delete', params={'id': fakeOSID, 'teamID': dppTeamID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [11],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 1}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/osinstances/add', params={'id': dppTeamID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [11],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [11],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [7],
# 'teamVersion': 1}

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/osinstances/delete', params={'id': dppTeamID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#200
#pprint(r.json())
#{'osInstanceAdminGateURI': 'ssh://fakeOs1.fake.lan',
# 'osInstanceApplicationsID': [11],
# 'osInstanceDescription': 'A fake OS',
# 'osInstanceEmbeddedOSInstancesID': [],
# 'osInstanceEmbeddingOSInstanceID': 1,
# 'osInstanceEnvironmentsID': [2],
# 'osInstanceID': 7,
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#200
#pprint(r.json())
#{'teamApplicationsID': [11],
# 'teamColorCode': '1f45de',
# 'teamDescription': 'Dev. DPP team',
# 'teamID': 11,
# 'teamName': 'DEV DPP',
# 'teamOSInstancesID': [],
# 'teamVersion': 1}

payload = '{"applicationName":"fake App name2", "applicationShortName":"fake app2", "applicationColorCode": "de26de", "applicationDescription":"This is fake app2", "applicationTeamID" : '+ str(dppTeamID) +', "applicationCompanyID": '+ str(oracleCmp)+', "applicationOSInstancesID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
fakeAppID2 = r.json().get('applicationID')
#200
# pprint(r.json())
#{'applicationColorCode': 'de26de',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'This is fake app2',
# 'applicationID': 12,
# 'applicationName': 'fake App name2',
# 'applicationOSInstancesID': [1,2],
# 'applicationShortName': 'fake app2',
# 'applicationTeamID': 1,
# 'applicationVersion': 1}

payload = '{"applicationID": '+ str(fakeAppID2) +',"applicationName":"updated fake App name2", "applicationShortName":"updated fake app2", "applicationColorCode": "de25de", "applicationDescription":"This is updated fake app2", "applicationTeamID" : '+ str(dppTeamID2) +', "applicationCompanyID": '+ str(spectralCmp)+', "applicationOSInstancesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload":payload})
fakeAppID2 = r.json().get('applicationID')
#200
# pprint(r.json())
#{'applicationColorCode': 'de25de',
# 'applicationCompanyID': 2,
# 'applicationDescription': 'This is updated fake app2',
# 'applicationID': 12,
# 'applicationName': 'updated fake App name2',
# 'applicationOSInstancesID': [2],
# 'applicationShortName': 'updated fake app2',
# 'applicationTeamID': 2,
# 'applicationVersion': 1}

ipAddressParams = {'id': fake_ipAddressID, 'osInstanceID': fakeOSID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/osInstance', params=ipAddressParams)
r.status_code
#200

ipAddressParams = {'id': fake_ipAddressID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.119",
#     "ipAddressFQDN": "Fake FQDN3"
#     "ipAddressOSInstancesID": 1,
#     "ipAddressSubnetID": 2
#}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/delete', params={'id': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/get', params={'id': fakeOSID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/delete', params={'id': solID})
r.status_code

#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/get', params={'id': solID})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/delete', params={'id': fake_subnet_ID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/delete', params={'id': fake_ipAddressID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params={'id': fake_ipAddressID})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/delete', params={'id': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get', params={'id': devilRareaID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/delete', params={'id': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/datacenters/get', params={'id': devilDCID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/delete', params={'id': dppID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/get', params={'id': dppID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/delete', params={'id': dppTeamID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/get', params={'id': dppTeamID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/delete', params={'id': qaEnv})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/get', params={'id': qaEnv})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/delete', params={'id': oracleCmp})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/get', params={'id': oracleCmp})
r.status_code
#404
