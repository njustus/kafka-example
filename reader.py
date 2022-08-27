import json

with open('versions.json') as f:
    data = json.load(f)
    versions = []
    for row in data:
        content = row['FileContent']
        lines = []
        for line in content['content']:
            lineContent = line['content']
            username = line['user']['username']
            lines.append("user: "+username+" : "+lineContent)
        versions.append(lines)
    
    
    for version in versions:
        print("==== new version ====")
        print('\n'.join(version))