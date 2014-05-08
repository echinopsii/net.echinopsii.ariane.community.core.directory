#!/usr/bin/python3

import getpass
import requests
import json
#from pprint import pprint

username = input("%-- >> Username : ")
password = getpass.getpass("%-- >> Password : ")
srvurl = input("%-- >> CC server url (like http://serverFQDN:6969/) : ")

# CREATE REQUESTS SESSION
s = requests.Session()
s.auth = (username, password)

r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications');
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

r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications/1');
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

applicationParams = {'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications/get', params=applicationParams);
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

applicationParams = {'name':"Tibco Rendez Vous"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications/get', params=applicationParams);
r.status_code
#404

applicationParams = {'name':"Tibco RendezVous"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications/get', params=applicationParams);
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

r = s.get(srvurl + 'CC/rest/directories/common/organisation/applications/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'






r = s.get(srvurl + 'CC/rest/directories/common/organisation/companies');
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

r = s.get(srvurl + 'CC/rest/directories/common/organisation/companies/1');
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

companyParams = {'id':"1"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/companies/get', params=companyParams);
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

companyParams = {'name':"Tibco"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/companies/get', params=companyParams);
r.status_code
#200
#pprint(r.json())
#{'companyApplicationsID': [1],
# 'companyDescription': 'Tibco',
# 'companyID': 1,
# 'companyName': 'Tibco',
# 'companyOSTypesID': [],
# 'companyVersion': 0}

r = s.get(srvurl + 'CC/rest/directories/common/organisation/companies/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'






r = s.get(srvurl + 'CC/rest/directories/common/organisation/environments');
r.status_code
#200
#pprint(r.json())
#{'environments': [{'environmentDescription': 'Development environment',
#                   'environmentID': 1,
#                   'environmentName': 'DEV',
#                   'environmentOSInstancesID': [1, 2, 3, 4, 5],
#                   'environmentVersion': 13}]}

r = s.get(srvurl + 'CC/rest/directories/common/organisation/environments/1');
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

envParams = {'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/environments/get', params=envParams);
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

envParams = {'name':"DEV"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/environments/get', params=envParams);
r.status_code
#200
#pprint(r.json())
#{'environmentDescription': 'Development environment',
# 'environmentID': 1,
# 'environmentName': 'DEV',
# 'environmentOSInstancesID': [1, 2, 3, 4, 5],
# 'environmentVersion': 13}

r = s.get(srvurl + 'CC/rest/directories/common/organisation/environments/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'







r = s.get(srvurl + 'CC/rest/directories/common/organisation/teams');
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

r = s.get(srvurl + 'CC/rest/directories/common/organisation/teams/1');
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

teamParams={'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/teams/get', params=teamParams);
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

teamParams={'name':"SRV UNIX"}
r = s.get(srvurl + 'CC/rest/directories/common/organisation/teams/get', params=teamParams);
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

r = s.get(srvurl + 'CC/rest/directories/common/organisation/teams/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'





r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters');
r.status_code
#200
#pprint(r.json())
#{'datacenters': [{'datacenterAddress': '26 rue de Belfort',
#                  'datacenterCountry': 'France',
#                  'datacenterDescription': 'dekatonshIVr lab',
#                  'datacenterGPSLat': 48.895308,
#                  'datacenterGPSLng': 2.246621,
#                  'datacenterID': 1,
#                  'datacenterMulticastAreasID': [1],
#                  'datacenterName': 'My little paradise',
#                  'datacenterSubnetsID': [1, 2, 3],
#                  'datacenterTown': 'Courbevoie',
#                  'datacenterVersion': 33,
#                  'datacenterZipCode': 92400}]}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters/1');
r.status_code
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterMulticastAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

dcParams = {'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters/get', params=dcParams);
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterMulticastAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

dcParams = {'name':'My little paradise'}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters/get', params=dcParams);
r.status_code
#200
#pprint(r.json())
#{'datacenterAddress': '26 rue de Belfort',
# 'datacenterCountry': 'France',
# 'datacenterDescription': 'dekatonshIVr lab',
# 'datacenterGPSLat': 48.895308,
# 'datacenterGPSLng': 2.246621,
# 'datacenterID': 1,
# 'datacenterMulticastAreasID': [1],
# 'datacenterName': 'My little paradise',
# 'datacenterSubnetsID': [1, 2, 3],
# 'datacenterTown': 'Courbevoie',
# 'datacenterVersion': 33,
# 'datacenterZipCode': 92400}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'






r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/multicastareas');
r.status_code
#200
#pprint(r.json())
#{'multicastAreas': [{'multicastAreaDatacentersID': [1],
#                     'multicastAreaDescription': 'lab01 multicast area',
#                     'multicastAreaID': 1,
#                     'multicastAreaName': 'angelsMind',
#                     'multicastAreaSubnetsID': [1],
#                     'multicastAreaVersion': 5}]}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/multicastareas/1');
r.status_code
#200
#pprint(r.json())
#{'multicastAreaDatacentersID': [1],
# 'multicastAreaDescription': 'lab01 multicast area',
# 'multicastAreaID': 1,
# 'multicastAreaName': 'angelsMind',
# 'multicastAreaSubnetsID': [1],
# 'multicastAreaVersion': 5}

mareaParams={'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/multicastareas/get', params=mareaParams);
r.status_code
#200
#pprint(r.json())
#{'multicastAreaDatacentersID': [1],
# 'multicastAreaDescription': 'lab01 multicast area',
# 'multicastAreaID': 1,
# 'multicastAreaName': 'angelsMind',
# 'multicastAreaSubnetsID': [1],
# 'multicastAreaVersion': 5}

mareaParams={'name':"angelsMind"}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/multicastareas/get', params=mareaParams);
r.status_code
#200
#pprint(r.json())
#{'multicastAreaDatacentersID': [1],
# 'multicastAreaDescription': 'lab01 multicast area',
# 'multicastAreaID': 1,
# 'multicastAreaName': 'angelsMind',
# 'multicastAreaSubnetsID': [1],
# 'multicastAreaVersion': 5}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/datacenters/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'







r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/subnets');
r.status_code
#200
#pprint(r.json())
#{'subnets': [{'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 LAN1',
#              'subnetID': 1,
#              'subnetIP': '192.168.33.0',
#              'subnetMask': '255.255.255.0',
#              'subnetMulticastAreaID': 1,
#              'subnetName': 'lab01.lan1',
#              'subnetOSInstancesID': [1, 2, 3],
#              'subnetType': 'LAN',
#              'subnetVersion': 23},
#             {'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 MAN',
#              'subnetID': 2,
#              'subnetIP': '192.168.34.0',
#              'subnetMask': '255.255.255.0',
#              'subnetMulticastAreaID': -1,
#              'subnetName': 'lab01.man',
#              'subnetOSInstancesID': [1, 4],
#              'subnetType': 'MAN',
#              'subnetVersion': 8},
#             {'subnetDatacentersID': [1],
#              'subnetDescription': 'lab01 WAN',
#              'subnetID': 3,
#              'subnetIP': '192.168.35.0',
#              'subnetMask': '255.255.255.0',
#              'subnetMulticastAreaID': -1,
#              'subnetName': 'lab01.wan',
#              'subnetOSInstancesID': [1, 5],
#              'subnetType': 'WAN',
#              'subnetVersion': 4}]}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/subnets/1');
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetMulticastAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

subnetParams={'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/subnets/get', params=subnetParams);
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetMulticastAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

subnetParams={'name':"lab01.lan1"}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/subnets/get', params=subnetParams);
r.status_code
#200
#pprint(r.json())
#{'subnetDatacentersID': [1],
# 'subnetDescription': 'lab01 LAN1',
# 'subnetID': 1,
# 'subnetIP': '192.168.33.0',
# 'subnetMask': '255.255.255.0',
# 'subnetMulticastAreaID': 1,
# 'subnetName': 'lab01.lan1',
# 'subnetOSInstancesID': [1, 2, 3],
# 'subnetType': 'LAN',
# 'subnetVersion': 23}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/network/subnets/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'




r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/osinstances');
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

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/osinstances/1');
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

osiParams = {'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/osinstances/get', params=osiParams);
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

osiParams = {'name':"hvirt.dekatonshIVr"}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/osinstances/get', params=osiParams);
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

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/osinstances/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'








r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/ostypes');
r.status_code
#200
#pprint(r.json())
#{'osTypes': [{'osTypeArchitecture': 'x86_64',
#              'osTypeCompanyID': 2,
#              'osTypeID': 1,
#              'osTypeName': 'Fedora 18',
#              'osTypeOSInstancesID': [1, 2, 3, 4, 5],
#              'osTypeVersion': 8}]}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/ostypes/1');
r.status_code
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

ostParams={'id':1}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/ostypes/get', params=ostParams);
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

ostParams={'name':"Fedora 18"}
r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/ostypes/get', params=ostParams);
#200
#pprint(r.json())
#{'osTypeArchitecture': 'x86_64',
# 'osTypeCompanyID': 2,
# 'osTypeID': 1,
# 'osTypeName': 'Fedora 18',
# 'osTypeOSInstancesID': [1, 2, 3, 4, 5],
# 'osTypeVersion': 8}

r = s.get(srvurl + 'CC/rest/directories/common/infrastructure/system/ostypes/get');
r.status_code
#500
#r.text
#'Request error: id and name are not defined. You must define one of these parameters'
