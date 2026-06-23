import re
import os
import glob

def split_path_data(path_data, max_len=10000):
    if len(path_data) <= max_len:
        return [path_data]

    # Split by M or m (Move To) commands, but keep the command
    parts = re.split(r'(?=[Mm])', path_data)

    results = []
    current_part = ""
    for part in parts:
        if len(current_part) + len(part) > max_len and current_part:
            results.append(current_part)
            current_part = part
        else:
            current_part += part
    if current_part:
        results.append(current_part)
    return results

def process_file(filepath):
    print(f"Processing {filepath}...")
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"  Error reading {filepath}: {e}")
        return

    def replace_path(match):
        full_tag = match.group(0)
        attrs = {}
        # Find all attributes like android:attr="value"
        # We search specifically for android:pathData to avoid matching other things
        # But we capture all attributes to preserve them.
        for attr_match in re.finditer(r'([\w:]+)="([^"]*)"', full_tag):
            attrs[attr_match.group(1)] = attr_match.group(2)

        path_data = attrs.get('android:pathData', '')
        if len(path_data) > 10000:
            print(f"  Found long path in {os.path.basename(filepath)}: {len(path_data)} chars")
            split_data = split_path_data(path_data, 10000)
            new_tags = []
            for data in split_data:
                tag_parts = ['<path']
                # Add pathData first or keep original order? Order doesn't strictly matter for XML but consistency is good.
                # We'll use the original attributes but replace pathData.
                for k, v in attrs.items():
                    if k == 'android:pathData':
                        tag_parts.append(f'android:pathData="{data}"')
                    else:
                        tag_parts.append(f'{k}="{v}"')
                tag_parts.append('/>')
                new_tags.append(" ".join(tag_parts))
            return "\n".join(new_tags)
        return full_tag

    # Match <path ... /> tags. Some might be multi-line.
    new_content = re.sub(r'<path\s+[^>]*/>', replace_path, content, flags=re.DOTALL)

    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"  Saved {filepath}")

# Process all XML files in drawable folder
files_to_process = glob.glob('app/src/main/res/drawable/*.xml')

for f in files_to_process:
    # Only process if file size is > 50KB to save time
    if os.path.getsize(f) > 50000:
        process_file(f)
