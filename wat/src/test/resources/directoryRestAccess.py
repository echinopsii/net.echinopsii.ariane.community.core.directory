#!/usr/bin/python3

import getpass
import requests
import json
# from pprint import pprint

username = input("%-- >> Username : ")
password = getpass.getpass("%-- >> Password : ")
srv_url = input("%-- >> Ariane server url (like http://serverFQDN:6969/) : ")

# CREATE REQUESTS SESSION
s = requests.Session()
s.auth = (username, password)

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications')
r.status_code
# 200

# pprint(r.json())
# {'applications': [{'applicationColorCode': '6092c4',
# 'applicationCompanyID': 1,
# 'applicationDescription': 'Multicast bus for business applications',
# 'applicationID': 1,
# 'applicationName': 'Tibco RendezVous',
# 'applicationOSInstancesID': [2, 3, 4, 5],
# 'applicationShortName': 'TibcoRV',
# 'applicationTeamID': 2,
# 'applicationVersion': 9},
# {'applicationColorCode': '5e647a',
# 'applicationCompanyID': 2,
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/name',
          params={'id': dppID, 'name': 'dPP'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with name dPP'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/name',
          params={'id': dppID, 'name': 'DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/shortName',
          params={'id': dppID, 'shortName': 'dPP'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with short name dPP'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/shortName',
          params={'id': dppID, 'shortName': 'DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/description',
          params={'id': dppID, 'description': 'another fake app'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with description another fake app'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/colorCode',
          params={'id': dppID, 'colorCode': 'ddffddff'})
r.status_code
#200
#r.text
#'Application 11 has been successfully updated with color code ddffddff'

payload = '{"applicationName":"fake App name", "applicationShortName":"fake app", "applicationColorCode": "dddddd",' \
          ' "applicationDescription":"This is fake app"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"applicationID": ' + str(fakeID) + ',"applicationName": "New Fake app name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"applicationID": ' + str(fakeID) + ', "applicationShortName": "New Fake app"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payl0oad = '{"applicationID": ' + str(fakeID) + ', "applicationColorCode": "de28de"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"applicationID": ' + str(fakeID) + ', "applicationDescription": "updated description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/applications/add',
          params={'id': tibcoCmp, 'applicationID': dppID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/applications/delete',
          params={'id': tibcoCmp, 'applicationID': dppID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/company',
          params={'id': dppID, 'companyID': spectralCmp})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/create',
          params={'name': 'Oracle', 'description': 'the devil company'})
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

payload = '{"companyName":"fake Comp name", "companyDescription":"This is fake Comp"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
fakeCompID = r.json().get('companyID')
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'This is fake Comp',
# 'companyID': 12,
# 'companyName': 'fake Comp name',
# 'companyOSTypesID': [],
# 'companyApplicationsID': []}

payload = '{"companyID": ' + str(fakeCompID) + ',"companyName": "New Fake Comp name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'This is fake Comp',
# 'companyID': 12,
# 'companyName': 'New Fake Comp name',
# 'companyOSTypesID': [],
# 'companyApplicationsID': []}

payload = '{"companyID": ' + str(fakeCompID) + ',"companyDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'updated Description',
# 'companyID': 12,
# 'companyName': 'New Fake Comp name',
# 'companyOSTypesID': [],
# 'companyApplicationsID': []}

payload = '{"companyID": ' + str(fakeCompID) + ',"companyApplicationsID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'updated Description',
# 'companyID': 12,
# 'companyName': 'New Fake Comp name',
# 'companyOSTypesID': [],
# 'companyApplicationsID': [1,2]}

payload = '{"applicationID": ' + str(fakeID) + ', "applicationCompanyID": ' + str(spectralCmp) + '}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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


r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/name',
          params={'id': oracleCmp, 'name': 'Oracle Corp'})
r.status_code
#200
r.text
#'Company 25 has been successfully updated with name Oracle Corp'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/description',
          params={'id': oracleCmp, 'description': 'The devil company'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/create',
          params={'name': 'HOM', 'description': 'Homologation environment'})
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Homologation environment',
# 'environmentID': 2,
# 'environmentName': 'HOM',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}
qaEnv = r.json().get('environmentID')

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/name',
          params={'id': qaEnv, 'name': 'QA'})
r.status_code
#200
r.text
#'Environment 2 has been successfully updated with name QA'

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/description',
          params={'id': qaEnv, 'description': 'QA environment'})
r.status_code
#200
r.text
#'Environment 2 has been successfully updated with name QA'

payload = '{"environmentName":"fake environment name", "environmentColorCode":"054d32",' \
          '"environmentDescription":"This is fake environment"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
fakeEnvID = r.json().get('environmentID')
#200()
# pprint(r.json())
#{'environmentColorCode': '054d32',
# 'environmentDescription': 'This is fake environment',
# 'environmentID': 1,
# 'environmentName': 'fake environment name',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}

payload = '{"environmentID": ' + str(fakeEnvID) + ',"environmentName": "New Fake environment name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d32',
# 'environmentDescription': 'This is fake environment',
# 'environmentID': 1,
# 'environmentName': 'New fake environment name',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}

payload = '{"environmentID": ' + str(fakeEnvID) + ',"environmentDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d32',
# 'environmentDescription': 'updated Description',
# 'environmentID': 1,
# 'environmentName': 'New fake environment name',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}

payload = '{"environmentID": ' + str(fakeEnvID) + ',"environmentColorCode": "054d33"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d33',
# 'environmentDescription': 'updated Description',
# 'environmentID': 1,
# 'environmentName': 'New fake environment name',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}


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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create',
          params={'name': 'DEV DPP', 'description': 'Dev. DPP team', 'colorCode': '1f45de'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create',
          params={'name': 'DEV DPP2', 'description': 'Dev. DPP team2', 'colorCode': '1f44de'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/name',
          params={'id': dppTeamID, 'name': 'DEV dpp'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/name',
          params={'id': dppTeamID, 'name': 'DEV DPP'})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/description',
          params={'id': dppTeamID, 'description': 'Dev. DPP team'})
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
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/create',
          params={'name': 'DEV DPP', 'description': 'Dev. DPP team', 'colorCode': '1f45de'})
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

payload = '{"teamName":"fake Team name", "teamColorCode":"054d32", "teamDescription":"This is fake Team"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
fakeTeamID = r.json().get('teamID')
#200()
# pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '054d32',
# 'teamDescription': 'This is fake Team',
# 'teamID': 1,
# 'teamName': 'fake Team name',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"teamID": ' + str(fakeTeamID) + ',"teamName": "New Fake Team name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '054d32',
# 'teamDescription': 'This is fake Team',
# 'teamID': 1,
# 'teamName': 'New fake Team name',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"teamID": ' + str(fakeTeamID) + ',"teamDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '054d32',
# 'teamDescription': 'updated Description',
# 'teamID': 1,
# 'teamName': 'New fake Team name',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"teamID": ' + str(fakeTeamID) + ',"teamColorCode": "054d33"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '054d33',
# 'teamDescription': 'updated Description',
# 'teamID': 1,
# 'teamName': 'New fake Team name',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"teamID": ' + str(fakeTeamID) + ', "teamApplicationsID" : [1, 2] }'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [1,2],
# 'teamColorCode': '054d33',
# 'teamDescription': 'updated Description',
# 'teamID': 1,
# 'teamName': 'New fake Team name',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"teamID": 50, "teamName" : "New Team name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
# Request Error : provided Team ID 50 was not found.

payload = '{"applicationID": ' + str(fakeID) + ', "applicationTeamID": ' + str(dppTeamID) + '}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/applications/add',
          params={'id': dppTeamID, 'applicationID': dppID})
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





r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations')
r.status_code
#200
#pprint(r.json())
#{'locations': [{'locationAddress': '26 rue de Belfort',
#                  'locationCountry': 'France',
#                  'locationDescription': 'dekatonshIVr lab',
#                  'locationGPSLat': 48.895308,
#                  'locationGPSLng': 2.246621,
#                  'locationID': 1,
#                  'locationRoutingAreasID': [1],
#                  'locationName': 'My little paradise',
#                  'locationSubnetsID': [1, 2, 3],
#                  'locationType' : 'DATACENTER',
#                  'locationTown': 'Courbevoie',
#                  'locationVersion': 33,
#                  'locationZipCode': 92400}]}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/1')
r.status_code
#pprint(r.json())
#{'locationAddress': '26 rue de Belfort',
# 'locationCountry': 'France',
# 'locationDescription': 'dekatonshIVr lab',
# 'locationGPSLat': 48.895308,
# 'locationGPSLng': 2.246621,
# 'locationID': 1,
# 'locationRoutingAreasID': [1],
# 'locationName': 'My little paradise',
# 'locationSubnetsID': [1, 2, 3],
# 'locationTown': 'Courbevoie',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 33,
# 'locationZipCode': 92400}

locParams = {'id': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params=locParams)
r.status_code
#200
#pprint(r.json())
#{'locationAddress': '26 rue de Belfort',
# 'locationCountry': 'France',
# 'locationDescription': 'dekatonshIVr lab',
# 'locationGPSLat': 48.895308,
# 'locationGPSLng': 2.246621,
# 'locationID': 1,
# 'locationRoutingAreasID': [1],
# 'locationName': 'My little paradise',
# 'locationSubnetsID': [1, 2, 3],
# 'locationTown': 'Courbevoie',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 33,
# 'locationZipCode': 92400}

locParams = {'name': 'My little paradise'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params=locParams)
r.status_code
#200
#pprint(r.json())
#{'locationAddress': '26 rue de Belfort',
# 'locationCountry': 'France',
# 'locationDescription': 'dekatonshIVr lab',
# 'locationGPSLat': 48.895308,
# 'locationGPSLng': 2.246621,
# 'locationID': 1,
# 'locationRoutingAreasID': [1],
# 'locationName': 'My little paradise',
# 'locationSubnetsID': [1, 2, 3],
# 'locationTown': 'Courbevoie',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 33,
# 'locationZipCode': 92400}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get')
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/create',
          params={'name': 'Somewhere in hell [DR]', 'address': "Devil's Island", 'zipCode': 666,
                  'town': "Devil's Island", 'tpye':'DATACENTER', 'country': 'France',
                  'gpsLatitude': 5.295366, 'gpsLongitude': -52.582179, 'description': 'A fantasy DR DC'})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': "Devil's Island",
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DR DC',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Somewhere in hell [DR]',
# 'locationSubnetsID': [],
# 'locationTown': "Devil's Island",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 666}
devilDCID = r.json().get('locationID')

payload = '{"locationName":"fake location name", "locationAddress": "Fake address", "locationZipCode": 422101, "locationTown":"Fake town", "locationType": "DATACENTER", "locationCountry":"fake country", "locationGPSLat": 5.295366, "locationGPSLng": -52.582179, "locationDescription":"This is fake location"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
fakeDcID = r.json().get('locationID')
#200
#pprint(r.json())
#{'locationAddress': "fake address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'This is fake location',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422101}


payload = '{"locationID": ' + str(fakeDcID) + ',"locationName": "New Fake location name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "fake address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'This is fake location',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'New fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422101}


payload = '{"locationID": ' + str(fakeDcID) + ',"locationDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "fake address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422101}


payload = '{"locationID": ' + str(fakeDcID) + ',"locationTown": "new location town"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "fake address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422101}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationType": "OFFICE"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "fake address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 1,
# 'locationZipCode': 422101}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationAddress": "new location address"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422101}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationZipCode": 422111}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationCountry": "new fake country"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'new fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationGPSLng": -51.60}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'new fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -51.60,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationGPSLat": 6.22}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'new fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 6.22,
# 'locationGPSLng': -51.60,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/name',
          params={'id': devilDCID, 'name': 'Hell (DR)'})
r.status_code
#200
r.text
#'Location 2 has been successfully updated with name Hell (DR)'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': "Devil's Island",
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DR DC',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': "Devil's Island",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 1,
# 'locationZipCode': 666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/description',
          params={'id': devilDCID, 'description': 'A fantasy DC for DR'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': "Devil's Island",
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DC for DR',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': "Devil's Island",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 2,
# 'locationZipCode': 666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/fullAddress',
          params={'id': devilDCID, 'address': 'dreyfus hole', 'zipCode': 666666, 'town': '666 Island',
                  'country': 'France'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': 'dreyfus hole',
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DC for DR',
# 'locationGPSLat': 5.295366,
# 'locationGPSLng': -52.582179,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': '666 Island',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 3,
# 'locationZipCode': 666666}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/gpsCoord',
          params={'id': devilDCID, 'gpsLatitude': 5.295666, 'gpsLongitude': -52.582169})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': 'dreyfus hole',
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DC for DR',
# 'locationGPSLat': 5.295666,
# 'locationGPSLng': -52.582169,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': '666 Island',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 5,
# 'locationZipCode': 666666}




r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas')
r.status_code
#200
#pprint(r.json())
#{'routingAreas': [{'routingAreaLocationsID': [1],
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
#{'routingAreaLocationsID': [1],
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
#{'routingAreaLocationsID': [1],
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
#{'routingAreaLocationsID': [1],
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create',
          params={'name': "devilsMindLAN", 'description': "666 mind", 'type': "LAN", 'multicast': "NOLIMIT"})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}
devilRareaID = r.json().get("routingAreaID")

payload = '{"routingAreaName":"fake routingArea name", "routingAreaMulticast": "NOLIMIT", "routingAreaType": "LAN",' \
          ' "routingAreaDescription":"This is fake routingArea"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
fakerAreaID = r.json().get('routingAreaID')
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'This is fake routingArea',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}


payload = '{"routingAreaID": ' + str(fakerAreaID) + ',"routingAreaName": "New Fake routingArea name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'This is fake routingArea',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'New fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 1}


payload = '{"routingAreaID": ' + str(fakerAreaID) + ',"routingAreaDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'updated description',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 2}


payload = '{"routingAreaID": ' + str(fakerAreaID) + ',"routingAreaType": "WAN"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'updated description',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'WAN',
# 'routingAreaVersion': 3}

payload = '{"routingAreaID": ' + str(fakerAreaID) + ',"routingAreaMulticast": "LIMT"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'updated description',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'LIMIT',
# 'routingAreaName': 'fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'WAN',
# 'routingAreaVersion': 4}

payload = '{"routingAreaID": ' + str(fakerAreaID) + ',"routingAreaLocationsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [1],
# 'routingAreaDescription': 'updated description',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'LIMIT',
# 'routingAreaName': 'fake routingArea name',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'WAN',
# 'routingAreaVersion': 5}

payload = '{"routingAreaName":"fake routingArea name2", "routingAreaMulticast": "NOLIMIT", "routingAreaType": "WAN",' \
          ' "routingAreaDescription":"This is fake routingArea", "routingAreaLocationsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
newfakerAreaID = r.json().get("routingAreaID")
#200
# pprint(r.json())
#{'routingAreaLocationsID': [1],
# 'routingAreaDescription': 'This is fake routingArea',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': 'fake routingArea name2',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'WAN',
# 'routingAreaVersion': 0}

payload = '{"routingAreaID": ' + str(newfakerAreaID) + ',"routingAreaName":"fake routingArea name3",' \
                                                       ' "routingAreaMulticast": "LIMIT", "routingAreaType": "LAN",' \
                                                       ' "routingAreaDescription":"This is updated fake routingArea",' \
                                                       ' "routingAreaLocationsID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [2],
# 'routingAreaDescription': 'This is updated fake routingArea',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'LIMIT',
# 'routingAreaName': 'fake routingArea name3',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 1}

payload = '{"routingAreaID": ' + str(newfakerAreaID) + ',"routingAreaName":"fake routingArea name3",' \
                                                       ' "routingAreaMulticast": "LIMIT", "routingAreaType": "LAN",' \
                                                       ' "routingAreaDescription":"This is updated fake routingArea",' \
                                                       ' "routingAreaLocationsID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas', params={"payload": payload})
#200
# pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'This is updated fake routingArea',
# 'routingAreaID': 1,
# 'routingAreaMulticast': 'LIMIT',
# 'routingAreaName': 'fake routingArea name3',
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 2}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create',
          params={'name': "devilsMindMAN", 'description': "666 mind", 'type': "MAN", 'multicast': "TOTO"})
r.status_code
#400
#r.text
#'Invalid multicast flag. Correct multicast flags values are : [NONE, FILTERED, NOLIMIT]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/create',
          params={'name': "devilsMindMAN", 'description': "666 mind", 'type': "MAN", 'multicast': "TOTO"})
r.status_code
#400
#r.text
#'Invalid type. Correct type values are : [LAN, MAN, WAN]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/routingareas/add',
          params={'id': devilDCID, 'routingareaID': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': 'dreyfus hole',
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DC for DR',
# 'locationGPSLat': 5.295666,
# 'locationGPSLng': -52.582169,
# 'locationID': 2,
# 'locationRoutingAreasID': [2],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': '666 Island',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 6,
# 'locationZipCode': 666666}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [2],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/update/routingareas/delete',
          params={'id': devilDCID, 'routingareaID': devilRareaID})
r.status_code
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
r.status_code
#200
#pprint(r.json())
#{'locationAddress': 'dreyfus hole',
# 'locationCountry': 'France',
# 'locationDescription': 'A fantasy DC for DR',
# 'locationGPSLat': 5.295666,
# 'locationGPSLng': -52.582169,
# 'locationID': 2,
# 'locationRoutingAreasID': [],
# 'locationName': 'Hell (DR)',
# 'locationSubnetsID': [],
# 'locationTown': '666 Island',
# 'locationType' : 'DATACENTER',
# 'locationVersion': 6,
# 'locationZipCode': 666666}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Devil's mind",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/name',
          params={'id': devilRareaID, 'name': 'Mind of devil'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': '666 mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/description',
          params={'id': devilRareaID, 'description': 'just a crazy mind'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/locations/add',
          params={'id': devilRareaID, 'locationID': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [2],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/locations/delete',
          params={'id': devilRareaID, 'locationID': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#{'routingAreaLocationsID': [],
# 'routingAreaDescription': 'just a crazy mind',
# 'routingAreaID': 2,
# 'routingAreaMulticast': 'NOLIMIT',
# 'routingAreaName': "Mind of devil",
# 'routingAreaSubnetsID': [],
# 'routingAreaType': 'LAN',
# 'routingAreaVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/locations/add',
          params={'id': devilRareaID, 'locationID': devilDCID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/type',
          params={'id': devilRareaID, 'type': 'MAN'})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/type',
          params={'id': devilRareaID, 'type': 'toto'})
r.status_code
#400
#r.text
#'Invalid type. Correct type values are : [LAN, MAN, WAN]'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/multicast',
          params={'id': devilRareaID, 'multicast': 'FILTERED'})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#...

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/update/multicast',
          params={'id': devilRareaID, 'multicast': 'TOTO'})
r.status_code
#400
#r.text
#'Invalid multicast flag. Correct multicast flags values are : [NONE, FILTERED, NOLIMIT]'



r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets')
r.status_code
#200
#pprint(r.json())
#{'subnets': [{'subnetLocationsID': [1],
#              'subnetDescription': 'lab01 LAN1',
#              'subnetID': 1,
#              'subnetIP': '192.168.33.0',
#              'subnetMask': '255.255.255.0',
#              'subnetRoutingAreaID': 1,
#              'subnetName': 'lab01.lan1',
#              'subnetOSInstancesID': [1, 2, 3],
#              'subnetType': 'LAN',
#              'subnetVersion': 23},
#             {'subnetLocationsID': [1],
#              'subnetDescription': 'lab01 MAN',
#              'subnetID': 2,
#              'subnetIP': '192.168.34.0',
#              'subnetMask': '255.255.255.0',
#              'subnetRoutingAreaID': -1,
#              'subnetName': 'lab01.man',
#              'subnetOSInstancesID': [1, 4],
#              'subnetType': 'MAN',
#              'subnetVersion': 8},
#             {'subnetLocationsID': [1],
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
#{'subnetLocationsID': [1],
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
#{'subnetLocationsID': [1],
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
#{'subnetLocationsID': [1],
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/create',
          params={'name': 'dummy.subnet', 'subnetIP': '123.123.48.0', 'subnetMask': '255.255.240.0',
                  'routingArea': devilRareaID, 'description': 'a fake subnet'})
r.status_code
dummy_subnetID = r.json().get('subnetID')

#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'dummy subnet',
# 'subnetID': 5,
# 'subnetIP': '123.123.48.0',
# 'subnetMask': '255.255.240.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.subnet',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/create',
          params={'name': 'fake.subnet', 'subnetIP': '192.168.66.0', 'subnetMask': '255.255.255.0',
                  'routingArea': devilRareaID, 'description': 'a fake subnet'})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/name',
          params={'id': fake_subnet_ID, 'name': 'fake.lan'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.66.0',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/subnetip',
          params={'id': fake_subnet_ID, 'subnetIP': '192.168.69.69'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.0',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/subnetmask',
          params={'id': fake_subnet_ID, 'subnetMask': '255.255.255.255'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'a fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/description',
          params={'id': fake_subnet_ID, 'description': 'A fake subnet'})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'LAN',
# 'subnetVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea',
          params={'id': fake_subnet_ID, 'routingareaID': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': 2,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'MAN',
# 'subnetVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#200
#pprint(r.json())
#....


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea',
          params={'id': fake_subnet_ID, 'routingareaID': -1})
r.status_code
#404
#r.text
#'Routing Area -1 not found.'

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/routingarea',
          params={'id': fake_subnet_ID, 'routingareaID': devilRareaID})
r.status_code
#200

payload = '{"subnetName":"fake subnet name", "subnetIP": "192.168.66.0", "subnetMask": "255.255.255.0",' \
          ' "subnetDescription":"This is fake subnet", "subnetRoutingAreaID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
fakeSubnetID = r.json().get('subnetID')
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'This is fake subnet',
#  'subnetID': 3,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 0}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetName": "New Fake subnet name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'This is fake subnet',
#  'subnetID': 3,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 1}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 2}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetIP": "123.123.40.0"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 3}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetMask": "255.255.255.0"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 3}


payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetRoutingAreaID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 2,
#  'subnetType': 'MAN',
#  'subnetVersion': 4}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetLocationsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [1],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 2,
#  'subnetType': 'MAN',
#  'subnetVersion': 4}


payload = '{"locationID": ' + str(fakeDcID) + ',"locationRoutingAreasID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'new fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 6.22,
# 'locationGPSLng': -51.60,
# 'locationID': 2,
# 'locationRoutingAreasID': [1],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [],
# 'locationTown': "new location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

payload = '{"locationID": ' + str(fakeDcID) + ',"locationSubnetsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new location address",
# 'locationCountry': 'new fake country',
# 'locationDescription': 'updated description',
# 'locationGPSLat': 6.22,
# 'locationGPSLng': -51.60,
# 'locationID': 2,
# 'locationRoutingAreasID': [1],
# 'locationName': 'fake location name',
# 'locationSubnetsID': [1],
# 'locationTown': "new location town",
# 'locationType' : 'DATACENTER',
# 'locationVersion': 0,
# 'locationZipCode': 422111}

payload = '{"locationID": ' + str(
    newfakeDcID) + ',"locationName":"new fake location2 name", "locationAddress": "new Fake address2", "locationZipCode": 422103, "locationTown":"new Fake town2", "locationType": "OFFICE", "locationCountry":"new fake country2", "locationGPSLat": 7.295366, "locationGPSLng": -54.582179, "locationDescription":"This is new fake location2", "locationRoutingAreasID": [2], "locationSubnetsID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new fake address2",
# 'locationCountry': 'new fake country2',
# 'locationDescription': 'This is new fake location2',
# 'locationGPSLat': 7.295366,
# 'locationGPSLng': -54.582179,
# 'locationID': 3,
# 'locationRoutingAreasID': [2],
# 'locationName': 'new fake location2 name',
# 'locationSubnetsID': [1,2],
# 'locationTown': "new location town2",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422103}


payload = '{"locationID": ' + str(
    newfakeDcID) + ',"locationName":"new fake location2 name", "locationAddress": "new Fake address2", "locationZipCode": 422103, "locationTown":"new Fake town2","locationType":"OFFICE", "locationCountry":"new fake country2", "locationGPSLat": 7.295366, "locationGPSLng": -54.582179, "locationDescription":"This is new fake location2", "locationRoutingAreasID": [], "locationSubnetsID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations', params={"payload": payload})
#200
# pprint(r.json())
#{'locationAddress': "new fake address2",
# 'locationCountry': 'new fake country2',
# 'locationDescription': 'This is new fake location2',
# 'locationGPSLat': 7.295366,
# 'locationGPSLng': -54.582179,
# 'locationID': 3,
# 'locationRoutingAreasID': [2],
# 'locationName': 'new fake location2 name',
# 'locationSubnetsID': [1,2],
# 'locationTown': "new location town2",
# 'locationType' : 'OFFICE',
# 'locationVersion': 0,
# 'locationZipCode': 422103}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress')
r.status_code
#200
#pprint(r.json())
#["ipAddresses": {
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 5
# },{
#     "ipAddressID": 2,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.122",
#     "ipAddressFQDN": "Fake FQDN2"
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 5
# }]

ipAddressParams = {'ipAddress': '123.123.48.123', 'fqdn': 'Fake FQDN', 'networkSubnet': dummy_subnetID,
                   'osInstance': -1}
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
#     "ipAddressOSInstanceID": -1,
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
#     "ipAddressOSInstanceID": -1,
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
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'fqdn': 'Fake FQDN'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'ipAddress': '123.123.48.123', 'subnetID': 1}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get', params=ipAddressParams)
r.status_code
#200
#pprint(r.json())
#{
#     "ipAddressID": 1,
#     "ipAddressVersion": 0,
#     "ipAddressIPA": "123.123.48.123",
#     "ipAddressFQDN": "Fake FQDN"
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 5
#}

ipAddressParams = {'id': fake_ipAddressID, 'ipAddress': '123.123.48.119'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/ipAddress',
          params=ipAddressParams)
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
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'id': fake_ipAddressID, 'fqdn': 'Fake FQDN3'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/fqdn',
          params=ipAddressParams)
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
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 1
#}

ipAddressParams = {'id': fake_ipAddressID, 'subnetID': fake_subnet_ID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/subnet',
          params=ipAddressParams)
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
#     "ipAddressOSInstanceID": -1,
#     "ipAddressSubnetID": 2
#}

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetIPAddressesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [1],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [2],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [1],
#  'subnetRoutingAreaID': 2,
#  'subnetType': 'MAN',
#  'subnetVersion': 5}

payload = '{"subnetName":"fake subnet name2", "subnetIP": "192.168.66.0", "subnetMask": "255.255.255.0",' \
          ' "subnetDescription":"This is fake subnet2", "subnetRoutingAreaID": 2, "subnetOSInstancesID": [1,2],' \
          ' "subnetLocationsID": [1], "subnetIPAddressesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
newfakeSubnetID = r.json().get("subnetID")
#200
# pprint(r.json())
# {'subnetLocationsID': [1],
#  'subnetDescription': 'This is fake subnet2',
#  'subnetID': 5,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [2],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'fake subnet name2',
#  'subnetOSInstancesID': [1, 2],
#  'subnetRoutingAreaID': 2,
#  'subnetType': 'MAN',
#  'subnetVersion': 0}

payload = '{"subnetID": ' + str(
    newfakeSubnetID) + ',"subnetName":"fake subnet name3", "subnetIP": "192.168.66.0", "subnetMask": "255.255.255.0",' \
                       ' "subnetDescription":"This is fake subnet3", "subnetRoutingAreaID": 1,' \
                       ' "subnetOSInstancesID": [1], "subnetLocationsID": [1], "subnetIPAddressesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [1],
#  'subnetDescription': 'This is fake subnet3',
#  'subnetID': 5,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [2],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'fake subnet name3',
#  'subnetOSInstancesID': [1],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 1}

payload = '{"subnetID": ' + str(
    newfakeSubnetID) + ',"subnetName":"fake subnet name3", "subnetIP": "192.168.66.0", "subnetMask": "255.255.255.0",' \
                       ' "subnetDescription":"This is fake subnet3", "subnetRoutingAreaID": 1,' \
                       ' "subnetOSInstancesID": [], "subnetLocationsID": [], "subnetIPAddressesID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [],
#  'subnetDescription': 'This is fake subnet3',
#  'subnetID': 5,
#  'subnetIP': '192.168.66.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'fake subnet name3',
#  'subnetOSInstancesID': [],
#  'subnetRoutingAreaID': 1,
#  'subnetType': 'MAN',
#  'subnetVersion': 2}

payload = '{"ipAddressIPA":"123.123.48.122", "ipAddressFQDN": "fakeFQDN",' \
          ' "ipAddressSubnetID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
fakeipAddressID = r.json().get('ipAddressID')
#200()
# pprint(r.json())
# {'ipAddressFQDN': 'fakeFQDN',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.122',
#  'ipAddressOSInstanceID': -1,
#  'ipAddressSubnetID': 1,
#  'ipAddressVersion': 0}

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressIPA": "123.123.48.130"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'fakeFQDN',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': -1,
#  'ipAddressSubnetID': 1,
#  'ipAddressVersion': 1}

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressFQDN": "Fake FQDN2"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'Fake FQDN2',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': -1,
#  'ipAddressSubnetID': 1,
#  'ipAddressVersion': 2}

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressSubnetID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'Fake FQDN2',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': -1,
#  'ipAddressSubnetID': 2,
#  'ipAddressVersion': 3}


payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressOSInstanceID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'Fake FQDN2',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': 2,
#  'ipAddressSubnetID': 2,
#  'ipAddressVersion': 4}

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressNICardID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'Fake FQDN2',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': 2,
#  'ipAddressNICardID': 2,
#  'ipAddressSubnetID': 2,
#  'ipAddressVersion': 4}

payload = '{"ipAddressIPA":"123.123.48.123", "ipAddressFQDN": "fakeFQDN2", "ipAddressSubnetID": 1,' \
          ' "ipAddressOSInstanceID": 2, "ipAddressNICardID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
fakeipAddressID = r.json().get('ipAddressID')
#200()
# pprint(r.json())
# {'ipAddressFQDN': 'fakeFQDN2',
#  'ipAddressID': 26,
#  'ipAddressIPA': '123.123.48.123',
#  'ipAddressOSInstanceID': 2,
#  'ipAddressNICardID': 1,
#  'ipAddressSubnetID': 1,
#  'ipAddressVersion': 0}

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressIPA":"123.123.48.123",' \
                                                      ' "ipAddressFQDN": "fakeFQDN3", ' \
                                                      '"ipAddressSubnetID": 1, "ipAddressOSInstanceID": 3,' \
                                                      ' "ipAddressNICardID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200()
# pprint(r.json())
# {'ipAddressFQDN': 'fakeFQDN3',
#  'ipAddressID': 26,
#  'ipAddressIPA': '123.123.48.123',
#  'ipAddressOSInstanceID': 3,
#  'ipAddressNICardID': 2,
#  'ipAddressSubnetID': 1,
#  'ipAddressVersion': 1}

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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/create',
          params={'name': 'Solaris', 'architecture': 'sparc-32'})
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

payload = '{"companyID": ' + str(fakeCompID) + ',"companyOSTypesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'updated Description',
# 'companyID': 12,
# 'companyName': 'New Fake Comp name',
# 'companyOSTypesID': [1],
# 'companyApplicationsID': [1,2]}

payload = '{"companyName": "New fake Comp name","companyDescription": "updated for Comp2", "companyOSTypesID": [1,2],' \
          ' "companyApplicationsID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
newFakeCompId = r.json().get("CompanyID")
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'updated for Comp2',
# 'companyID': 12,
# 'companyName': 'New Fake Comp name',
# 'companyOSTypesID': [1,2],
# 'companyApplicationsID': [2]}

payload = '{"osTypeName":"fake osType name", "osTypeArchitecture":"fake architecture"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
fakeosTypeID = r.json().get('osTypeID')
#200()
# pprint(r.json())
# {'osTypeArchitecture': 'fake architecture',
#  'osTypeCompanyID': -1,
#  'osTypeID': 2,
#  'osTypeName': 'fake osType name',
#  'osTypeOSInstancesID': [],
#  'osTypeVersion': 0}

payload = '{"osTypeID": ' + str(fakeosTypeID) + ',"osTypeName": "New Fake osType name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# {'osTypeArchitecture': 'fake architecture',
#  'osTypeCompanyID': -1,
#  'osTypeID': 2,
#  'osTypeName': 'New Fake osType name',
#  'osTypeOSInstancesID': [],
#  'osTypeVersion': 1}

payload = '{"osTypeID": ' + str(fakeosTypeID) + ',"osTypeArchitecture": "updated fake architecture"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# {'osTypeArchitecture': 'updated fake architecture',
#  'osTypeCompanyID': -1,
#  'osTypeID': 2,
#  'osTypeName': 'New Fake osType name',
#  'osTypeOSInstancesID': [],
#  'osTypeVersion': 2}

payload = '{"osTypeID": ' + str(fakeosTypeID) + ',"osTypeOSInstancesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# {'osTypeArchitecture': 'updated fake architecture',
#  'osTypeCompanyID': -1,
#  'osTypeID': 2,
#  'osTypeName': 'New Fake osType name',
#  'osTypeOSInstancesID': [2],
#  'osTypeVersion': 2}

payload = '{"osTypeID": ' + str(fakeosTypeID) + ',"osTypeCompanyID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# {'osTypeArchitecture': 'updated fake architecture',
#  'osTypeCompanyID': 1,
#  'osTypeID': 2,
#  'osTypeName': 'New Fake osType name',
#  'osTypeOSInstancesID': [2],
#  'osTypeVersion': 3}

payload = '{"osTypeName":"fake osType name2", "osTypeArchitecture":"fake architecture2",' \
          ' "osTypeOSInstancesID": [2], "osTypeCompanyID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
newfakeosTypeID = r.json().get("osTypeID")
#200
# pprint(r.json())
# {'osTypeArchitecture': 'fake architecture2',
#  'osTypeCompanyID': 1,
#  'osTypeID': 4,
#  'osTypeName': 'fake osType name2',
#  'osTypeOSInstancesID': [2],
#  'osTypeVersion': 0}

#--------------------------------------------------------------

payload = '{"osTypeID": ' + str(newfakeosTypeID) + ',"osTypeName":"fake osType name3",' \
                                                   ' "osTypeArchitecture":"fake architecture3", ' \
                                                   '"osTypeOSInstancesID": [3], "osTypeCompanyID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# {'osTypeArchitecture': 'fake architecture3',
#  'osTypeCompanyID': 2,
#  'osTypeID': 4,
#  'osTypeName': 'fake osType name3',
#  'osTypeOSInstancesID': [3],
#  'osTypeVersion': 1}

payload = '{"osTypeID": ' + str(newfakeosTypeID) + ',"osTypeName":"new fake osType name2",' \
                                                   ' "osTypeArchitecture":"new fake architecture2", "osTypeOSInstancesID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.text)
# {'osTypeArchitecture': 'new fake architecture2',
#  'osTypeCompanyID': -1,
#  'osTypeID': 4,
#  'osTypeName': 'new fake osType name2',
#  'osTypeOSInstancesID': [],
#  'osTypeVersion': 1}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/name',
          params={'id': solID, 'name': 'Solaris OS'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/architecture',
          params={'id': solID, 'architecture': 'x86_64'})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes/update/company',
          params={'id': solID, 'companyID': oracleCmp})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/ostypes/delete',
          params={'id': oracleCmp, 'ostypeID': solID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/companies/update/ostypes/add',
          params={'id': oracleCmp, 'ostypeID': solID})
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
#                  'osInstanceIPAddressesID': [],
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
#                  'osInstanceIPAddressesID': [],
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
#                  'osInstanceIPAddressesID': [],
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
#                  'osInstanceIPAddressesID': [],
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
#                  'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/create',
          params={'name': 'fakeOs', 'adminGateURI': 'ssh://fakeOs.fake.lan', 'description': 'a fake os'})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
fakeOSID = r.json().get("osInstanceID")

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/create',
          params={'name': 'fakeOs2', 'adminGateURI': 'ssh://fakeOs.fake2.lan', 'description': 'a fake os2'})
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

payload = '{"subnetID": ' + str(fakeSubnetID) + ',"subnetOSInstancesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets', params={"payload": payload})
#200
# pprint(r.json())
# {'subnetLocationsID': [1],
#  'subnetDescription': 'updated Description',
#  'subnetID': 3,
#  'subnetIP': '123.123.40.0',
#  'subnetIPAddressesID': [],
#  'subnetMask': '255.255.255.0',
#  'subnetName': 'New Fake subnet name',
#  'subnetOSInstancesID': [1],
#  'subnetRoutingAreaID': 2,
#  'subnetType': 'MAN',
#  'subnetVersion': 5}

payload = '{"applicationID": ' + str(fakeID) + ',"applicationOSInstancesID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"teamID": ' + str(fakeTeamID) + ',"teamOSInstancesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [1,2],
# 'teamColorCode': '054d33',
# 'teamDescription': 'updated Description',
# 'teamID': 1,
# 'teamName': 'New fake Team name',
# 'teamOSInstancesID': [1],
# 'teamVersion': 0}

payload = '{"teamName": "New fake Team2 name", "teamColorCode":"054d34", teamDescription": "updated for Team2",' \
          '"teamOSInstancesID": [1,2], "teamApplicationsID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
newFakeTeamId = r.json().get("TeamID")
#200
# pprint(r.json())
#{'teamApplicationsID': [2],
# 'teamColorCode': '054d34',
# 'teamDescription': 'updated for Team2',
# 'teamID': 2,
# 'teamName': 'New fake Team2 name',
# 'teamOSInstancesID': [1,2],
# 'teamVersion': 0}

payload = '{"teamID": ' + str(
    newFakeTeamId) + ',"teamName": "New updated fake Team name","teamDescription": "new updated for Team2",' \
                     '"teamColorCode": "054d35", "teamOSInstancesID": [2], "teamApplicationsID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [1,2],
# 'teamColorCode': '054d35',
# 'teamDescription': 'new updated for Team2',
# 'teamID': 2,
# 'teamName': 'New updated fake Team',
# 'teamOSInstancesID': [2],
# 'teamVersion': 0}


payload = '{"teamID": ' + str(
    newFakeTeamId) + ',"teamName": "New updated fake Team name","teamDescription": "new updated for Team2",' \
                     ' "teamColorCode": "054d35", "teamOSInstancesID": [], "teamApplicationsID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/teams', params={"payload": payload})
#200
# pprint(r.json())
#{'teamApplicationsID': [],
# 'teamColorCode': '054d35',
# 'teamDescription': 'new updated for Team2',
# 'teamID': 2,
# 'teamName': 'New updated fake Team',
# 'teamOSInstancesID': [],
# 'teamVersion': 0}

payload = '{"environmentID": ' + str(fakeEnvID) + ',"environmentOSInstancesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d33',
# 'environmentDescription': 'updated Description',
# 'environmentID': 1,
# 'environmentName': 'New fake environment name',
# 'environmentOSInstancesID': [1],
# 'environmentVersion': 0}

payload = '{"environmentName": "New fake environment2 name", "environmentColorCode":"054d34", environmentDescription": "updated for environment2", "environmentOSInstancesID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
newfakeEnvID = r.json().get("environmentID")
#200
# pprint(r.json())
#{'environmentColorCode': '054d34',
# 'environmentDescription': 'updated for environment2',
# 'environmentID': 2,
# 'environmentName': 'New fake environment2 name',
# 'environmentOSInstancesID': [1,2],
# 'environmentVersion': 0}

payload = '{"environmentID": ' + str(
    newfakeEnvID) + ',"environmentName": "New updated fake environment name","environmentDescription": "new updated for environment2", "environmentColorCode": "054d35", "environmentOSInstancesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d35',
# 'environmentDescription': 'new updated for environment2',
# 'environmentID': 2,
# 'environmentName': 'New updated fake environment',
# 'environmentOSInstancesID': [2],
# 'environmentVersion': 0}


payload = '{"environmentID": ' + str(
    newfakeEnvID) + ',"environmentName": "New updated fake environment name","environmentDescription": "new updated for environment2", "environmentColorCode": "054d35", "environmentOSTypesID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
#{'environmentColorCode': '054d35',
# 'environmentDescription': 'new updated for environment2',
# 'environmentID': 2,
# 'environmentName': 'New updated fake environment',
# 'environmentOSInstancesID': [],
# 'environmentVersion': 0}

payload = '{"environmentID": 50 , "environmentOSInstancesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/environments', params={"payload": payload})
#200
# pprint(r.json())
# Request Error : provided Environment ID 50 was not found.

payload = '{"osInstanceName":"fake osInstance name", "osInstanceAdminGateURI": "Fake URI", ' \
          '"osInstanceDescription": "This is fake osInstance"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
fakeosInstanceID = r.json().get('osInstanceID')
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'This is fake osInstance',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': -1,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'fake osInstance name',
#  'osInstanceOSTypeID': -1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 0}


payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceName": "New Fake osInstance name"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'This is fake osInstance',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': -1,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': -1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 1}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceDescription": "updated Description"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': -1,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': -1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 2}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceAdminGateURI": "updated URI"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': -1,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': -1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 3}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceOSTypeID": 1}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': -1,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 4}


payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceEmbeddingOSInstanceID": 2}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceSubnetsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceEmbeddedOSInstancesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceApplicationsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceTeamsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [1],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceEnvironmentsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [1],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [1],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceIPAddressesID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [1],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [1],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [1],
#  'osInstanceVersion': 5}

payload = '{"osInstanceID": ' + str(fakeosInstanceID) + ',"osInstanceNICardsID": [1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'updated URI',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'updated Description',
#  'osInstanceEmbeddedOSInstancesID': [1],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [1],
#  'osInstanceID': 3,
#  'osInstanceIPAddressesID': [1],
#  'osInstanceNICardsID': [1],
#  'osInstanceName': 'New Fake osInstance name',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [1],
#  'osInstanceTeamsID': [1],
#  'osInstanceVersion': 5}

payload = '{"osInstanceName":"fake osInstance name2", "osInstanceAdminGateURI": "Fake URI2", ' \
          '"osInstanceDescription": "This is fake osInstance2", "osInstanceOSTypeID": 1,' \
          ' "osInstanceEmbeddingOSInstanceID": 2, "osInstanceSubnetsID": [3], "osInstanceTeamsID": [1],' \
          ' "osInstanceEnvironmentsID": [1], "osInstanceApplicationsID": [1], "osInstanceEmbeddedOSInstancesID": [1],' \
          ' "osInstanceIPAddressesID": [2], "osInstanceNICardsID":[1]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
newfakeosInstanceID = r.json().get("osInstanceID")
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI2',
#  'osInstanceApplicationsID': [1],
#  'osInstanceDescription': 'This is fake osInstance2',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [1],
#  'osInstanceID': 4,
#  'osInstanceIPAddressesID': [2],
#  'osInstanceNICardsID' : [2],
#  'osInstanceName': 'fake osInstance name2',
#  'osInstanceOSTypeID': 1,
#  'osInstanceSubnetsID': [3],
#  'osInstanceTeamsID': [1],
#  'osInstanceVersion': 0}

payload = '{"osInstanceID": ' + str(
    newfakeosInstanceID) + ',"osInstanceName":"fake osInstance name3", "osInstanceAdminGateURI": "Fake URI3", ' \
                           '"osInstanceDescription": "This is fake osInstance3", "osInstanceOSTypeID": 2, ' \
                           '"osInstanceEmbeddingOSInstanceID": 2, "osInstanceSubnetsID": [3,4],' \
                           ' "osInstanceTeamsID": [1,4], "osInstanceEnvironmentsID": [1,3],' \
                           ' "osInstanceApplicationsID": [3], "osInstanceEmbeddedOSInstancesID": [2],' \
                           ' "osInstanceIPAddressesID": [3], "osInstanceNICardsID": [3]}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI3',
#  'osInstanceApplicationsID': [3],
#  'osInstanceDescription': 'This is fake osInstance3',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [1, 3],
#  'osInstanceID': 4,
#  'osInstanceIPAddressesID': [3],
#  'osInstanceNICardsID' : [3],
#  'osInstanceName': 'fake osInstance name3',
#  'osInstanceOSTypeID': 2,
#  'osInstanceSubnetsID': [3, 4],
#  'osInstanceTeamsID': [1, 4],
#  'osInstanceVersion': 1}

payload = '{"osInstanceID": ' + str(
    newfakeosInstanceID) + ',"osInstanceName":"fake osInstance name2", "osInstanceAdminGateURI": "Fake URI2",' \
                           ' "osInstanceDescription": "This is fake osInstance2", "osInstanceOSTypeID": 2,' \
                           ' "osInstanceEmbeddingOSInstanceID": 2, "osInstanceSubnetsID": [], "osInstanceTeamsID": [],' \
                           ' "osInstanceEnvironmentsID": [], "osInstanceApplicationsID": [],' \
                           ' "osInstanceEmbeddedOSInstancesID": [], "osInstanceIPAddressesID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances', params={"payload": payload})
#200
# pprint(r.json())
# {'osInstanceAdminGateURI': 'Fake URI2',
#  'osInstanceApplicationsID': [],
#  'osInstanceDescription': 'This is fake osInstance2',
#  'osInstanceEmbeddedOSInstancesID': [],
#  'osInstanceEmbeddingOSInstanceID': 2,
#  'osInstanceEnvironmentsID': [],
#  'osInstanceID': 4,
#  'osInstanceIPAddressesID': [],
#  'osInstanceName': 'fake osInstance name2',
#  'osInstanceOSTypeID': 2,
#  'osInstanceSubnetsID': [],
#  'osInstanceTeamsID': [],
#  'osInstanceVersion': 2}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/name',
          params={'id': fakeOSID, 'name': 'fakeOs1'})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/description',
          params={'id': fakeOSID, 'description': 'A fake OS'})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/admingateuri',
          params={'id': fakeOSID, 'adminGateURI': "ssh://fakeOs1.fake.lan"})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': -1,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ostype',
          params={'id': fakeOSID, 'ostID': solID})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddingOSInstance',
          params={'id': fakeOSID, 'osiID': 1})
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
# 'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'hvirt.dekatonshIVr',
# 'osInstanceOSTypeID': 1,
# 'osInstanceSubnetsID': [1, 2, 3],
# 'osInstanceTeamsID': [1],
# 'osInstanceVersion': 4}

r = s.get(
    srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddedOSInstances/delete',
    params={'id': 1, 'osiID': fakeOSID})
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
# 'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/embeddedOSInstances/add',
          params={'id': 1, 'osiID': fakeOSID})
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
# 'osInstanceIPAddressesID': [],
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/subnets/add',
          params={'id': fakeOSID, 'subnetID': fake_subnet_ID})
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
# 'osInstanceIPAddressesID': [],
# 'osInstanceName': 'fakeOs1',
# 'osInstanceOSTypeID': 2,
# 'osInstanceSubnetsID': [4],
# 'osInstanceTeamsID': [],
# 'osInstanceVersion': 0}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetIPAddressesID': [],
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [7],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/subnets/delete',
          params={'id': fakeOSID, 'subnetID': fake_subnet_ID})
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
#{'subnetLocationsID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetIPAddressesID': [],
# 'subnetMask': '255.255.255.255',
# 'subnetRoutingAreaID': -1,
# 'subnetName': 'fake.lan',
# 'subnetOSInstancesID': [],
# 'subnetType': 'MAN',
# 'subnetVersion': 12}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/add',
          params={'id': fake_subnet_ID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
# 'subnetDescription': 'A fake subnet',
# 'subnetID': 4,
# 'subnetIP': '192.168.69.69',
# 'subnetIPAddressesID': [],
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/delete',
          params={'id': fake_subnet_ID, 'osiID': fakeOSID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code
#200
#pprint(r.json())
#{'subnetLocationsID': [],
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
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/update/osinstances/add',
          params={'id': fakeOSID, 'osiID': fakeOSID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/applications/add',
          params={'id': fakeOSID, 'applicationID': dppID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/applications/delete',
          params={'id': fakeOSID, 'applicationID': dppID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/add',
          params={'id': dppID, 'osiID': fakeOSID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/delete',
          params={'id': dppID, 'osiID': fakeOSID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/applications/update/osinstances/add',
          params={'id': dppID, 'osiID': fakeOSID})
r.status_code
#200

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/environments/add',
          params={'id': fakeOSID, 'environmentID': qaEnv})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/environments/delete',
          params={'id': fakeOSID, 'environmentID': qaEnv})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ipAddresses/add',
          params={'id': fakeOSID, 'ipAddressID': fake_ipAddressID})
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
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get',
          params={'id': fake_ipAddressID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/ipAddresses/delete',
          params={'id': fakeOSID, 'ipAddressID': fake_ipAddressID})
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


r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/add',
          params={'id': qaEnv, 'osiID': fakeOSID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/delete',
          params={'id': qaEnv, 'osiID': fakeOSID})
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
r = s.get(srv_url + 'ariane/rest/directories/common/organisation/environments/update/osinstances/add',
          params={'id': qaEnv, 'osiID': fakeOSID})
r.status_code
#200


r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/teams/add',
          params={'id': fakeOSID, 'teamID': dppTeamID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/teams/delete',
          params={'id': fakeOSID, 'teamID': dppTeamID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/osinstances/add',
          params={'id': dppTeamID, 'osiID': fakeOSID})
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

r = s.get(srv_url + 'ariane/rest/directories/common/organisation/teams/update/osinstances/delete',
          params={'id': dppTeamID, 'osiID': fakeOSID})
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

payload = '{"applicationName":"fake App name2", "applicationShortName":"fake app2", "applicationColorCode": "de26de",' \
          '"applicationDescription":"This is fake app2", "applicationTeamID" : ' + str(
    dppTeamID) + ', "applicationCompanyID": ' + str(oracleCmp) + ', "applicationOSInstancesID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"applicationID": ' + str(
    fakeAppID2) + ',"applicationName":"updated fake App name2", "applicationShortName":"updated fake app2",' \
                  ' "applicationColorCode": "de25de", "applicationDescription":"This is updated fake app2", ' \
                  '"applicationTeamID" : ' + str(
    dppTeamID2) + ', "applicationCompanyID": ' + str(spectralCmp) + ', "applicationOSInstancesID": [2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/applications', params={"payload": payload})
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

payload = '{"companyID": ' + str(
    newFakeCompId) + ',"companyName": "New updated fake Comp name","companyDescription": "new updated for Comp2", ' \
                     '"companyOSTypesID": [2], "companyApplicationsID": [1,2]}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'new updated for Comp2',
# 'companyID': 12,
# 'companyName': 'New updated Fake Comp name',
# 'companyOSTypesID': [2],
# 'companyApplicationsID': [1,2]}

payload = '{"companyID": ' + str(
    newFakeCompId) + ',"companyName": "New updated fake Comp name","companyDescription": "new updated for Comp2",' \
                     '"companyOSTypesID": [], "companyApplicationsID": []}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'new updated for Comp2',
# 'companyID': 12,
# 'companyName': 'New updated Fake Comp name',
# 'companyOSTypesID': [],
# 'companyApplicationsID': []}

payload = '{"companyID": ' + str(
    newFakeCompId) + ',"companyName": "Latest updated fake Comp name","companyDescription": "updated for Comp3"}'
r = s.post(srv_url + 'ariane/rest/directories/common/organisation/companies', params={"payload": payload})
#200
# pprint(r.json())
#{'companyVersion': 1,
# 'companyDescription': 'new updated for Comp3',
# 'companyID': 12,
# 'companyName': 'Latest updated Fake Comp name',
# 'companyOSTypesID': -1,
# 'companyApplicationsID': -1}

ipAddressParams = {'id': fake_ipAddressID, 'osInstanceID': fakeOSID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/update/osInstance',
          params=ipAddressParams)
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


nicParams = {'name': "fake NIC", "macAddress": "00:00:00:00:00:10"}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/create', params=nicParams)
r.status_code
fakeNICID = r.json().get('nicardID')
#200
# pprint(r.json())
# {'niCardDuplex': None,
#  'niCardID': 13,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:10',
#  'niCardMtu': 0,
#  'niCardName': 'fake NIC',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 0,
#  'niCardVersion': 0}

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/system/osinstances/update/nicards/add',
          params={'id': fakeOSID, 'niCardID': fakeNICID})
r.status_code

nicParams = {'id': fakeNICID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/get', params=nicParams)
r.status_code
#200
# pprint(r.json())
# {'niCardDuplex': None,
#  'niCardID': 13,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:10',
#  'niCardMtu': 0,
#  'niCardName': 'fake NIC',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 0,
#  'niCardVersion': 0}

nicParams = {'id': fakeNICID, 'macAddress': '00:00:00:00:00:12'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/macAddress', params=nicParams)
r.status_code
#200

nicParams = {'id': fakeNICID, 'name': 'fake name'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/macAddress', params=nicParams)
r.status_code
#200


nicParams = {'id': fakeNICID, 'speed': '20'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/speed', params=nicParams)
r.status_code
#200

nicParams = {'id': fakeNICID, 'duplex': 'fake duplex'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/duplex', params=nicParams)
r.status_code
#200


nicParams = {'id': fakeNICID, 'mtu': '40'}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/mtu', params=nicParams)
r.status_code
#200


nicParams = {'id': fakeNICID, 'osInstanceID': fakeOSID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/osInstance', params=nicParams)
r.status_code
#200


nicParams = {'id': fakeNICID, 'ipAddressID': fakeipAddressID}
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard/update/ipAddress', params=nicParams)
r.status_code
#200

payload = '{"ipAddressID": ' + str(fakeipAddressID) + ',"ipAddressNICardID": '+ str(fakeNICID)+'}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress', params={"payload": payload})
#200
# pprint(r.json())
# {'ipAddressFQDN': 'Fake FQDN2',
#  'ipAddressID': 25,
#  'ipAddressIPA': '123.123.48.130',
#  'ipAddressOSInstanceID': 2,
#  'ipAddressNICardID': 2,
#  'ipAddressSubnetID': 2,
#  'ipAddressVersion': 4}

payload = '{"niCardMacAddress":"00:00:00:00:00:05", "niCardName": "fakeNIC"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
fakeniCardID = r.json().get('niCardID')
#200()
# pprint(r.json())
# {'niCardDuplex': None,
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:05',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 0,
#  'niCardVersion': 0}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardName": "FakeNIC2"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200
# pprint(r.json())
# {'niCardDuplex': None,
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:05',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 0,
#  'niCardVersion': 1}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardMacAddress": "00:00:00:00:00:06"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
# 200
# pprint(r.json())
# {'niCardDuplex': None,
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 0,
#  'niCardVersion': 2}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardDuplex": "Fake NIC duplex"}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200
# pprint(r.json())
# {'niCardDuplex': 'Fake NIC duplex',
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 20,
#  'niCardVersion': 3}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardSpeed": 20}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200
# pprint(r.json())
# {'niCardDuplex': 'Fake NIC duplex',
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 20,
#  'niCardVersion': 4}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardMtu": 40}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200
# pprint(r.json())
# {'niCardDuplex': 'Fake NIC duplex',
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': -1,
#  'niCardSpeed': 40,
#  'niCardVersion': 5}

payload = '{"niCardID": ' + str(fakeniCardID) + ',"niCardOSInstanceID": 3}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200()
# pprint(r.json())
# {'niCardDuplex': 'Fake NIC duplex',
#  'niCardID': 10,
#  'niCardIPAddressID': -1,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': 3,
#  'niCardSpeed': 0,
#  'niCardVersion': 6}

payload = '{"niCardID": ' + str(fakeniCardID) + ', "niCardIPAddressID": 14}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
#200()
# pprint(r.json())
# {'niCardDuplex': 'Fake NIC duplex',
#  'niCardID': 10,
#  'niCardIPAddressID': 14,
#  'niCardMacAddress': '00:00:00:00:00:06',
#  'niCardMtu': 0,
#  'niCardName': 'FakeNIC2',
#  'niCardOSInstanceID': 3,
#  'niCardSpeed': 0,
#  'niCardVersion': 7}

payload = '{"niCardName":"fake NICard name2", "niCardMacAddress":"00:00:00:00:00:07", "niCardSpeed": 20,' \
          ' "niCardMtu": 20, "niCardDuplex": "fake duplex", "niCardOSInstanceID" : 14, "niCardIPAddressID": 14}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/network/niCard', params={"payload": payload})
newfakenicardID = r.json().get("niCardID")
#200
# pprint(r.json())
# {'niCardDuplex': 'fake duplex',
#  'niCardID': 12,
#  'niCardIPAddressID': 14,
#  'niCardMacAddress': '00:00:00:00:00:07',
#  'niCardMtu': 20,
#  'niCardName': 'fake NICard name2',
#  'niCardOSInstanceID': 14,
#  'niCardSpeed': 20,
#  'niCardVersion': 0}

payload = '{"niCardID": ' + str(
    12) + ',"niCardName":"fake NICard name3", "niCardMacAddress":"00:00:00:00:00:08", "niCardSpeed": 30,' \
          ' "niCardMtu": 30, "niCardDuplex": "fake duplex2", "niCardOSInstanceID" : 10, "niCardIPAddressID": 10}'
r = s.post(srv_url + 'ariane/rest/directories/common/infrastructure/system/ostypes', params={"payload": payload})
#200
# pprint(r.json())
# pprint(r.json())
# {'niCardDuplex': 'fake duplex2',
#  'niCardID': 12,
#  'niCardIPAddressID': 10,
#  'niCardMacAddress': '00:00:00:00:00:08',
#  'niCardMtu': 30,
#  'niCardName': 'fake NICard name3',
#  'niCardOSInstanceID': 10,
#  'niCardSpeed': 30,
#  'niCardVersion': 1}

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

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/delete',
          params={'id': fake_subnet_ID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/subnets/get', params={'id': fake_subnet_ID})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/delete',
          params={'id': fake_ipAddressID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/ipAddress/get',
          params={'id': fake_ipAddressID})
r.status_code

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/delete',
          params={'id': devilRareaID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/routingareas/get',
          params={'id': devilRareaID})
r.status_code
#404

r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/delete',
          params={'id': devilDCID})
r.status_code
#200
r = s.get(srv_url + 'ariane/rest/directories/common/infrastructure/network/locations/get', params={'id': devilDCID})
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
