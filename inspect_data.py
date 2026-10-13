import json
import sys

with open('scratch_pptx.json', 'r', encoding='utf-8') as f:
    slides = json.load(f)

with open('pptx_full_dump.txt', 'w', encoding='utf-8') as out:
    out.write(f"TOTAL SLIDES: {len(slides)}\n\n")
    for s in slides:
        out.write(f"=== SLIDE {s['slide']} ===\n")
        for line in s['content']:
            out.write(f"  {line}\n")
        out.write("\n")

with open('scratch_modul3.json', 'r', encoding='utf-8') as f:
    m3 = json.load(f)

with open('modul3_full_dump.txt', 'w', encoding='utf-8') as out:
    out.write("=== MODUL 3 PARAGRAPHS ===\n\n")
    for i, p in enumerate(m3['paragraphs']):
        out.write(f"[{i}] {p}\n\n")
    out.write("\n=== MODUL 3 TABLES ===\n\n")
    for idx, table in enumerate(m3['tables']):
        out.write(f"--- Table {idx+1} (Rows: {len(table)}) ---\n")
        for r in table:
            out.write(f"  {' | '.join(r)}\n")
        out.write("\n")

print("Dumped pptx_full_dump.txt and modul3_full_dump.txt successfully!")
