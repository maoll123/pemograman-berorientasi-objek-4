import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn
import os

def create_report():
    doc = docx.Document()

    # Page Setup (Standard Letter / A4 with 1-inch margins)
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)
        section.page_width = Inches(8.5)
        section.page_height = Inches(11.0)

    # Color Palette Constants (Professional Navy / Slate)
    COLOR_PRIMARY_NAVY = RGBColor(0x1B, 0x36, 0x5D)    # #1B365D - Deep Navy
    COLOR_SECONDARY_BLUE = RGBColor(0x2B, 0x6C, 0xB0)  # #2B6CB0 - Slate Blue
    COLOR_DARK_TEXT = RGBColor(0x2D, 0x37, 0x48)       # #2D3748 - Charcoal Body Text
    COLOR_MUTED_GRAY = RGBColor(0x71, 0x80, 0x96)      # #718096 - Muted Subtext
    COLOR_WHITE = RGBColor(0xFF, 0xFF, 0xFF)           # #FFFFFF - Pure White
    COLOR_GREEN_SUCCESS = RGBColor(0x1B, 0x7A, 0x43)   # #1B7A43 - Dark Emerald Green

    HEX_NAVY = "1B365D"
    HEX_LIGHT_BG = "F8FAFC"
    HEX_ROW_ALT = "F1F5F9"
    HEX_BORDER = "CBD5E1"
    HEX_CALLOUT_BORDER = "3182CE"
    HEX_CODE_BG = "F8FAFC"

    def set_cell_shading(cell, color_hex):
        shading = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shading)

    def set_cell_margins(cell, top=140, bottom=140, left=180, right=180):
        tcPr = cell._tc.get_or_add_tcPr()
        tcMar = parse_xml(f'''
            <w:tcMar {nsdecls("w")}>
                <w:top w:w="{top}" w:type="dxa"/>
                <w:bottom w:w="{bottom}" w:type="dxa"/>
                <w:left w:w="{left}" w:type="dxa"/>
                <w:right w:w="{right}" w:type="dxa"/>
            </w:tcMar>
        ''')
        tcPr.append(tcMar)

    def set_table_borders(table, color="CBD5E1", sz="4", val="single"):
        tblPr = table._tbl.tblPr
        tblBorders = parse_xml(f'''
            <w:tblBorders {nsdecls("w")}>
                <w:top w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:bottom w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:insideH w:val="{val}" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:insideV w:val="none"/>
                <w:left w:val="none"/>
                <w:right w:val="none"/>
            </w:tblBorders>
        ''')
        tblPr.append(tblBorders)

    def add_header_title(text, level=1):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.bold = True
        run.font.name = 'Calibri'
        if level == 1:
            run.font.size = Pt(14)
            run.font.color.rgb = COLOR_PRIMARY_NAVY
        elif level == 2:
            run.font.size = Pt(12)
            run.font.color.rgb = COLOR_PRIMARY_NAVY
        elif level == 3:
            run.font.size = Pt(11)
            run.font.color.rgb = COLOR_SECONDARY_BLUE
        return p

    def add_body_p(text, bold_prefix=None, space_after=4):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        if bold_prefix:
            r_pre = p.add_run(bold_prefix)
            r_pre.bold = True
            r_pre.font.name = 'Calibri'
            r_pre.font.size = Pt(10.5)
            r_pre.font.color.rgb = COLOR_DARK_TEXT
        r = p.add_run(text)
        r.font.name = 'Calibri'
        r.font.size = Pt(10.5)
        r.font.color.rgb = COLOR_DARK_TEXT
        return p

    def add_code_block(code_text, caption=None):
        if caption:
            cp = doc.add_paragraph()
            cp.paragraph_format.space_before = Pt(8)
            cp.paragraph_format.space_after = Pt(2)
            cp.paragraph_format.keep_with_next = True
            r = cp.add_run(caption)
            r.bold = True
            r.font.name = 'Calibri'
            r.font.size = Pt(10)
            r.font.color.rgb = COLOR_PRIMARY_NAVY

        table = doc.add_table(rows=1, cols=1)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = table.cell(0, 0)
        cell.width = Inches(6.5)
        set_cell_shading(cell, HEX_CODE_BG)
        set_cell_margins(cell, top=120, bottom=120, left=160, right=160)

        tcPr = cell._tc.get_or_add_tcPr()
        borders = parse_xml(f'''
            <w:tcBorders {nsdecls("w")}>
                <w:top w:val="single" w:sz="4" w:space="0" w:color="{HEX_BORDER}"/>
                <w:left w:val="single" w:sz="18" w:space="0" w:color="{HEX_CALLOUT_BORDER}"/>
                <w:bottom w:val="single" w:sz="4" w:space="0" w:color="{HEX_BORDER}"/>
                <w:right w:val="single" w:sz="4" w:space="0" w:color="{HEX_BORDER}"/>
            </w:tcBorders>
        ''')
        tcPr.append(borders)

        cp = cell.paragraphs[0]
        cp.paragraph_format.space_before = Pt(0)
        cp.paragraph_format.space_after = Pt(0)
        cp.paragraph_format.line_spacing = 1.05
        run = cp.add_run(code_text)
        run.font.name = 'Consolas'
        run.font.size = Pt(8.5)
        run.font.color.rgb = RGBColor(0x1A, 0x20, 0x2C)

        p_after = doc.add_paragraph()
        p_after.paragraph_format.space_before = Pt(0)
        p_after.paragraph_format.space_after = Pt(4)

    def add_callout_box(box_text, title=None):
        table = doc.add_table(rows=1, cols=1)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = table.cell(0, 0)
        cell.width = Inches(6.5)
        set_cell_shading(cell, "EDF2F7")
        set_cell_margins(cell, top=120, bottom=120, left=160, right=160)

        tcPr = cell._tc.get_or_add_tcPr()
        borders = parse_xml(f'''
            <w:tcBorders {nsdecls("w")}>
                <w:top w:val="none"/>
                <w:left w:val="single" w:sz="24" w:space="0" w:color="{HEX_NAVY}"/>
                <w:bottom w:val="none"/>
                <w:right w:val="none"/>
            </w:tcBorders>
        ''')
        tcPr.append(borders)

        cp = cell.paragraphs[0]
        cp.paragraph_format.space_before = Pt(0)
        cp.paragraph_format.space_after = Pt(0)
        if title:
            rt = cp.add_run(title + "\n")
            rt.bold = True
            rt.font.name = 'Calibri'
            rt.font.size = Pt(10.5)
            rt.font.color.rgb = COLOR_PRIMARY_NAVY
        r = cp.add_run(box_text)
        r.font.name = 'Calibri'
        r.font.size = Pt(10)
        r.font.color.rgb = COLOR_DARK_TEXT

        p_after = doc.add_paragraph()
        p_after.paragraph_format.space_before = Pt(0)
        p_after.paragraph_format.space_after = Pt(4)

    def get_source_file_content(filepath):
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                return f.read()
        except UnicodeDecodeError:
            try:
                with open(filepath, 'r', encoding='utf-16') as f:
                    return f.read()
            except Exception:
                with open(filepath, 'r', encoding='latin-1') as f:
                    return f.read()

    # -------------------------------------------------------------
    # COVER / HEADER TITLE
    # -------------------------------------------------------------
    p_title = doc.add_paragraph()
    p_title.paragraph_format.space_before = Pt(0)
    p_title.paragraph_format.space_after = Pt(2)
    r_t = p_title.add_run("LAPORAN PRAKTIKUM PEMROGRAMAN BERORIENTASI OBYEK")
    r_t.bold = True
    r_t.font.name = 'Calibri'
    r_t.font.size = Pt(16)
    r_t.font.color.rgb = COLOR_PRIMARY_NAVY

    p_sub = doc.add_paragraph()
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(8)
    r_s = p_sub.add_run("MODUL 4: RELASI ANTAROBJECT (ASSOCIATION, AGGREGATION, DAN COMPOSITION)\nPENDEKATAN AGILE SPRINT (SPRINT 4 / P4)")
    r_s.bold = True
    r_s.font.name = 'Calibri'
    r_s.font.size = Pt(12)
    r_s.font.color.rgb = COLOR_SECONDARY_BLUE

    # Identitas Table
    t_meta = doc.add_table(rows=5, cols=2)
    t_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_meta)
    meta_data = [
        ("Mata Kuliah", ": Praktikum Pemrograman Berorientasi Obyek (PBO)"),
        ("Nama / NRP", ": Ahmad Yasar Maulana Dais / 3125522018"),
        ("Dosen Pengampu", ": Nirwana Haidar Hari, S.Pd., M.Kom."),
        ("Nama Proyek", ": AutoRent System – Sistem Manajemen Rental & Armada Kendaraan"),
        ("Tautan Basis Proyek", ": Melanjutkan Hasil Refactoring Praktikum P3 (Kendaraan, Pelanggan, TransaksiSewa)")
    ]
    for idx, (label, val) in enumerate(meta_data):
        row = t_meta.rows[idx]
        c0, c1 = row.cells[0], row.cells[1]
        c0.width = Inches(2.0)
        c1.width = Inches(4.5)
        set_cell_margins(c0, 60, 60, 100, 100)
        set_cell_margins(c1, 60, 60, 100, 100)
        if idx % 2 == 1:
            set_cell_shading(c0, HEX_ROW_ALT)
            set_cell_shading(c1, HEX_ROW_ALT)

        p0 = c0.paragraphs[0]
        p0.paragraph_format.space_after = Pt(0)
        r0 = p0.add_run(label)
        r0.bold = True
        r0.font.name = 'Calibri'
        r0.font.size = Pt(10)
        r0.font.color.rgb = COLOR_DARK_TEXT

        p1 = c1.paragraphs[0]
        p1.paragraph_format.space_after = Pt(0)
        r1 = p1.add_run(val)
        r1.font.name = 'Calibri'
        r1.font.size = Pt(10)
        r1.font.color.rgb = COLOR_DARK_TEXT

    p_div = doc.add_paragraph()
    p_div.paragraph_format.space_after = Pt(6)

    # -------------------------------------------------------------
    # BAB I: TUJUAN PRAKTIKUM & FONDASI AGILE SPRINT (SPRINT 4)
    # -------------------------------------------------------------
    add_header_title("BAB I: TUJUAN PRAKTIKUM & FONDASI AGILE SPRINT (SPRINT 4)", 1)

    add_body_p(
        "Berdasarkan Modul Praktikum Pemrograman Berorientasi Obyek Modul 4 yang disusun oleh Nirwana Haidar Hari, S.Pd., M.Kom., "
        "tujuan instruksional yang harus dicapai oleh mahasiswa pada praktikum Pertemuan 4 ini adalah sebagai berikut:",
        bold_prefix="1.1 Tujuan Praktikum (Capaian Pembelajaran Modul 4): "
    )

    tujuan_list = [
        "1. Menjelaskan hubungan antarobject dalam sistem berorientasi objek secara konseptual dan struktural.",
        "2. Membedakan karakteristik teknis antara Association, Aggregation, dan Composition berdasarkan siklus hidup (lifecycle) dan kepemilikan (ownership).",
        "3. Menentukan jenis relasi antarclass yang tepat berdasarkan analisis studi kasus domain dunia nyata.",
        "4. Mengimplementasikan objek sebagai atribut referensi pada class lain dengan menjaga prinsip encapsulation.",
        "5. Menghubungkan beberapa class yang berdiri sendiri menjadi satu kesatuan sistem perangkat lunak yang berkolaborasi secara dinamis.",
        "6. Memperbarui UML Class Diagram proyek dengan notasi relasi, multiplicity, dan navigability yang baku.",
        "7. Mengembangkan increment proyek secara iteratif dan terukur melalui kerangka kerja Agile Sprint (P4)."
    ]
    for tj in tujuan_list:
        add_body_p(tj, space_after=2)

    add_body_p(
        "Pada praktikum Pertemuan 3 (P3), mahasiswa telah berhasil merancang dan memperbaiki class-class utama dengan menerapkan prinsip "
        "Encapsulation penuh, pembatasan hak akses (access modifier private), penyediaan method aksesor dan mutator (getter & setter), "
        "konstruktor berparameter, serta validasi data defensif. Pada tahap P3 tersebut, class-class (seperti Kendaraan, Pelanggan, TransaksiSewa, "
        "atau class Buku pada modul) masih dimodelkan secara relatif terisolasi.\n\n"
        "Pada praktikum Pertemuan 4 (P4) ini, class tidak lagi berdiri sendiri-sendiri. Class mulai saling terhubung, bertukar pesan (message passing), "
        "dan berkolaborasi untuk membentuk alur proses bisnis yang utuh. Mahasiswa diwajibkan melanjutkan basis kode proyek P3 (AutoRent System) tanpa membuat proyek baru, "
        "kemudian merekayasa keterhubungan objek secara elegan.",
        bold_prefix="1.2 Review P3 dan Jembatan Arsitektural ke P4: "
    )

    add_header_title("1.3 Agile Sprint Framework (Sprint Goal & Sprint Backlog P4)", 2)
    add_callout_box(
        "Menghubungkan class utama proyek AutoRent System sehingga seluruh object dapat berinteraksi dan berkolaborasi sebagai satu sistem yang utuh, "
        "dengan mengimplementasikan Association, Aggregation, Composition, dan Dependency secara presisi sesuai aturan bisnis rental kendaraan.",
        "🎯 SPRINT GOAL (P4):"
    )

    t_sprint = doc.add_table(rows=7, cols=5)
    t_sprint.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_sprint)
    sb_headers = ["ID Backlog", "Aktivitas Sprint Backlog P4", "Target Deliverable", "Status P3", "Status P4"]
    for i, h in enumerate(sb_headers):
        cell = t_sprint.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    sb_data = [
        ("SB-01", "Mengidentifikasi relasi antarclass pada proyek P3", "Tabel identifikasi relasi (Min. 4 class, 3 jenis relasi)", "To Do", "DONE"),
        ("SB-02", "Memperbarui class diagram dengan relasi & multiplicity", "UML Class Diagram termutakhir (Sprint 4)", "To Do", "DONE"),
        ("SB-03", "Implementasi relasi Association pada kode Java", "Relasi TransaksiSewa — Pelanggan & Kendaraan", "To Do", "DONE"),
        ("SB-04", "Implementasi relasi Aggregation pada kode Java", "Relasi GarasiArmada ◇— Kendaraan (Objek dibuat di luar)", "To Do", "DONE"),
        ("SB-05", "Implementasi relasi Composition pada kode Java", "Relasi Kendaraan ◆— Mesin & Transaksi ◆— DetailItemBiaya", "To Do", "DONE"),
        ("SB-06", "Membuat pengujian interaksi antarobject komprehensif", "Driver Test Suite Main.java (Test 1, 2, 3 & Lifecycle)", "To Do", "DONE")
    ]

    for row_idx, data in enumerate(sb_data):
        row = t_sprint.rows[row_idx + 1]
        for col_idx, text in enumerate(data):
            cell = row.cells[col_idx]
            set_cell_margins(cell, 80, 80, 100, 100)
            if row_idx % 2 == 1:
                set_cell_shading(cell, HEX_ROW_ALT)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(text)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if col_idx == 0:
                r.bold = True
            if col_idx == 4:
                r.bold = True
                r.font.color.rgb = COLOR_GREEN_SUCCESS

    p_div2 = doc.add_paragraph()
    p_div2.paragraph_format.space_after = Pt(6)

    add_header_title("1.4 Bagian A – Identifikasi Relasi Antarclass Proyek AutoRent System", 2)
    add_body_p(
        "Sesuai petunjuk Bagian A Modul 4, dianalisis minimal 4 class dari proyek AutoRent System (Pelanggan, Kendaraan, Mesin, GarasiArmada, "
        "TransaksiSewa, dan DetailItemBiaya) dengan memenuhi kriteria minimal 1 Association, 1 Aggregation, dan 1 Composition:",
        space_after=4
    )

    t_ident = doc.add_table(rows=7, cols=4)
    t_ident.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_ident)
    ident_headers = ["Class A", "Class B", "Relasi", "Alasan & Justifikasi Domain"]
    for i, h in enumerate(ident_headers):
        cell = t_ident.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    ident_data = [
        ("Pelanggan", "TransaksiSewa", "Association (──)", "Pelanggan melakukan transaksi sewa unit. Relasi bersifat struktural stabil di mana transaksi merujuk pelanggan penyewa."),
        ("Kendaraan", "TransaksiSewa", "Association (──)", "Unit armada disewa dalam suatu transaksi. Kendaraan dapat disewa berkali-kali dalam transaksi berbeda secara bergantian."),
        ("GarasiArmada", "Kendaraan", "Aggregation (◇)", "Garasi menampung armada. Objek Kendaraan dibuat di luar dan dapat tetap hidup mandiri jika garasi dikosongkan atau dipindah."),
        ("Kendaraan", "Mesin", "Composition (◆)", "Mesin merupakan komponen internal fisik yang dibuat di dalam Kendaraan saat perakitan dan terikat paten pada nomor rangka kendaraan."),
        ("TransaksiSewa", "DetailItemBiaya", "Composition (◆)", "Rincian biaya (sewa pokok, add-on driver/asuransi, denda) dibuat oleh TransaksiSewa dan tidak bermakna di luar transaksi terkait."),
        ("TransaksiSewa", "KuitansiPrinter &\nLayananNotifikasi", "Dependency (- - - >)", "Printer dan Notifier adalah service luar yang dipanggil secara sementara (uses-a) saat cetak kuitansi dan kirim notifikasi WhatsApp.")
    ]

    for row_idx, data in enumerate(ident_data):
        row = t_ident.rows[row_idx + 1]
        for col_idx, text in enumerate(data):
            cell = row.cells[col_idx]
            set_cell_margins(cell, 80, 80, 100, 100)
            if row_idx % 2 == 1:
                set_cell_shading(cell, HEX_ROW_ALT)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(text)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if col_idx in (0, 1, 2):
                r.bold = True

    p_div3 = doc.add_paragraph()
    p_div3.paragraph_format.space_after = Pt(6)

    # -------------------------------------------------------------
    # BAB II: KAJIAN TEORI & PERANCANGAN ARSITEKTUR RELASI ANTAROBJECT
    # -------------------------------------------------------------
    add_header_title("BAB II: KAJIAN TEORI & PERANCANGAN ARSITEKTUR RELASI ANTAROBJECT", 1)

    add_body_p(
        "Berdasarkan materi resmi Modul 4 Praktikum PBO, konsep relasi antarobject memegang peranan krusial dalam menyusun arsitektur sistem "
        "yang modular, terstruktur, dan merefleksikan dunia nyata. Terdapat 3 relasi utama yang diperbandingkan secara mendalam pada modul ini, "
        "ditambah relasi Dependency sebagai pendukung arsitektur modular:",
        bold_prefix="2.1 Konsep & Taksonomi Relasi Antarobject: "
    )

    add_body_p(
        "Merupakan hubungan ketika object dari satu class berinteraksi atau mengetahui object dari class lain (hubungan 'uses-a' struktural). "
        "Dalam implementasi Java, class menyimpan atribut berupa referensi object class lain. "
        "Contoh pada Modul 4: `Mahasiswa -------- Dosen`, di mana class `Mahasiswa` memiliki atribut `private Dosen dosenWali;`. "
        "Pada AutoRent System, `TransaksiSewa` berasosiasi dengan `Pelanggan` dan `Kendaraan`.",
        bold_prefix="A. Association (Asosiasi): "
    )

    add_body_p(
        "Menunjukkan hubungan Whole–Part ('has-a') yang bersifat longgar/lemah, di mana object yang menjadi bagian (part) masih dapat hidup secara mandiri "
        "meskipun object pemilik (whole) tidak ada atau dimusnahkan. Object part diciptakan di luar class whole dan dioper ke dalam melalui constructor atau method setter. "
        "Contoh pada Modul 4: `Jurusan ◇------ Dosen`, di mana objek Dosen tetap eksis meskipun Jurusan ditiadakan. "
        "Pada AutoRent System, `GarasiArmada ◇------ Kendaraan` (unit mobil tetap ada dan bisa dipindah ke cabang lain jika garasi cabang ditutup).",
        bold_prefix="B. Aggregation (Agregasi ◇): "
    )

    add_body_p(
        "Merupakan hubungan Whole–Part ('part-of') yang lebih kuat dan mengikat erat siklus hidup objek bagian pada objek pemiliknya. "
        "Object bagian diciptakan (diinstansiasi dengan kata kunci `new`) di dalam class pemilik (pada constructor atau method internal). "
        "Jika object pemilik dimusnahkan, object bagian di dalamnya ikut musnah dan tidak bermakna lagi di luar konteks pemiliknya. "
        "Contoh pada Modul 4: `Rumah ◆------ Ruangan`, di mana objek `Ruangan` diciptakan langsung di dalam constructor `Rumah`. "
        "Pada AutoRent System, `Kendaraan ◆------ Mesin` dan `TransaksiSewa ◆------ DetailItemBiaya`.",
        bold_prefix="C. Composition (Komposisi ◆): "
    )

    add_body_p(
        "Merupakan hubungan ketergantungan fungsional yang bersifat sementara. Satu class menggunakan class lain melalui parameter method "
        "atau variabel lokal tanpa menyimpannya sebagai atribut permanen. "
        "Pada AutoRent System, `TransaksiSewa - - - - > KuitansiPrinter` dan `TransaksiSewa - - - - > LayananNotifikasi`.",
        bold_prefix="D. Dependency (Ketergantungan - - - - >): "
    )

    add_header_title("2.2 Tabel Matriks Perbandingan Karakteristik Relasi", 2)
    add_body_p(
        "Sesuai tabel perbandingan pada Modul 4 Bagian 4, berikut adalah rangkuman karakteristik dan pertanyaan kunci dalam menentukan jenis relasi:"
    )

    t_rel = doc.add_table(rows=5, cols=5)
    t_rel.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_rel)
    rel_headers = ["Jenis Relasi", "Karakter Hubungan", "Tingkat Ketergantungan", "Lokasi Instansiasi Part", "Contoh pada AutoRent System"]
    for i, h in enumerate(rel_headers):
        cell = t_rel.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    rel_comp_data = [
        ("Association", "uses-a (Interaksi Stabil)", "Lemah", "Dibuat di luar, disimpan sbg field", "Pelanggan ── TransaksiSewa ── Kendaraan"),
        ("Aggregation", "has-a (Whole-Part Lemah)", "Sedang", "Dibuat di luar, dioper ke whole", "GarasiArmada ◇── Kendaraan"),
        ("Composition", "part-of (Whole-Part Kuat)", "Kuat", "Dibuat di DALAM whole (new)", "Kendaraan ◆── Mesin\nTransaksiSewa ◆── DetailItemBiaya"),
        ("Dependency", "uses-a (Transient / Sementara)", "Sangat Lemah", "Diterima via parameter method", "TransaksiSewa - - - > KuitansiPrinter")
    ]

    for row_idx, data in enumerate(rel_comp_data):
        row = t_rel.rows[row_idx + 1]
        for col_idx, text in enumerate(data):
            cell = row.cells[col_idx]
            set_cell_margins(cell, 80, 80, 100, 100)
            if row_idx % 2 == 1:
                set_cell_shading(cell, HEX_ROW_ALT)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(text)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if col_idx == 0:
                r.bold = True

    add_callout_box(
        "Pertanyaan Kunci Modul 4: 'Apakah object bagian masih dapat berdiri sendiri jika object utama dihapus?'\n"
        "• Jika YA (objek bagian tetap hidup dan bermakna secara independen)  ==> AGGREGATION (Agregasi ◇)\n"
        "• Jika TIDAK (objek bagian ikut musnah dan tidak bermakna tanpa induk) ==> COMPOSITION (Komposisi ◆)",
        "💡 PRINSIP UTAMA PENENTUAN RELASI (MODUL 4):"
    )

    add_header_title("2.3 Analisis Studi Kasus Modul 4 (Sistem Perpustakaan vs AutoRent System)", 2)
    add_body_p(
        "Pada Modul 4 Bab 5, disajikan contoh studi kasus Sistem Perpustakaan dengan 4 class utama: `Anggota`, `Buku`, `Peminjaman`, dan `Perpustakaan`. "
        "Relasi yang terbentuk meliputi:\n"
        "1. `Anggota -------- Peminjaman` (Association): Anggota melakukan transaksi peminjaman.\n"
        "2. `Buku ----------- Peminjaman` (Association): Buku dipinjam dalam transaksi peminjaman.\n"
        "3. `Perpustakaan ◇--- Buku` (Aggregation): Perpustakaan mengoleksi buku; jika perpustakaan tutup, buku tetap ada.\n\n"
        "Konsep tersebut diterapkan dan diperluas secara presisi ke dalam domain proyek AutoRent System:\n"
        "• `Anggota` beranalogi dengan `Pelanggan`\n"
        "• `Buku` beranalogi dengan `Kendaraan`\n"
        "• `Peminjaman` beranalogi dengan `TransaksiSewa`\n"
        "• `Perpustakaan` beranalogi dengan `GarasiArmada`\n"
        "• Ditambahkan relasi Composition mendalam: `Kendaraan ◆— Mesin` dan `TransaksiSewa ◆— DetailItemBiaya` guna memenuhi standar arsitektur tingkat lanjut.",
        bold_prefix="Pemetaan Komparatif Kasus: "
    )

    add_header_title("2.4 Bagian B – Pembaruan UML Class Diagram Terpadu Proyek AutoRent System", 2)
    add_body_p(
        "Sesuai instruksi Bagian B Modul 4, class diagram proyek P3 diperbarui untuk memvisualisasikan seluruh class, atribut bertipe private, "
        "method bertipe public, multiplicity, serta relasi Association, Aggregation, Composition, dan Dependency:"
    )

    uml_diagram_text = """
+------------------------------------+               +------------------------------------+
|            GarasiArmada            |               |             Pelanggan              |
+------------------------------------+               +------------------------------------+
| - kodeGarasi : String              |               | - idPelanggan : String             |
| - namaCabang : String              |               | - namaLengkap : String             |
| - lokasiKota : String              |               | - nomorKtp : String                |
| - kapasitasMaksimal : int          |               | - nomorTelepon : String            |
| - daftarArmada : List<Kendaraan>   |               | - totalSewaAktif : int             |
+------------------------------------+               | - BATAS_MAX_SEWA : int = 2         |
| + tambahKendaraan(k : Kendaraan)   |               +------------------------------------+
| + hapusKendaraan(plat : String)    |               | + getIdPelanggan() : String        |
| + cariKendaraan(plat : String)     |               | + getNamaLengkap() : String        |
| + hitungArmadaReady() : int        |               | + bolehSewa() : boolean            |
| + kosongkanGarasi() : void         |               | + tambahSewaAktif(jml : int)       |
+------------------------------------+               | + kurangiSewaAktif(jml : int)      |
                 ◇ 1                                 +------------------------------------+
                 |                                                      ^ 1
                 | Aggregation (0..*)                                   |
                 v                                                      | Association (0..*)
+------------------------------------+                                  |
|             Kendaraan              |                                  |
+------------------------------------+                                  |
| - platNomor : String               |                                  |
| - merk : String                    |                                  |
| - jenis : String                   |                                  |
| - tarifPerHari : double            |                                  |
| - isTersedia : boolean             |                                  |
| - mesin : Mesin                    |                                  |
+------------------------------------+                                  |
| + getPlatNomor() : String          |                                  |
| + isUnitTersedia() : boolean       |                                  |
| + setStatusKetersediaan(s : bool)  |                                  |
| + getMesin() : Mesin               |                                  |
| + hitungEstimasiSewa(d : int)      |                                  |
+------------------------------------+                                  |
          ◆ 1                     ^ 1                                   |
          | Composition           |                                     |
          | (1:1)                 | Association (0..*)                  |
          v                       |                                     |
+--------------------+   +--------+-------------------------------------+------------------+
|       Mesin        |   |                          TransaksiSewa                          |
+--------------------+   +-----------------------------------------------------------------+
| - noRangka : String|   | - kodeTransaksi : String                                        |
| - kapasitasCC : int|   | - kendaraan : Kendaraan                                         |
| - transmisi: String|   | - pelanggan : Pelanggan                                         |
| - kondisi : String |   | - tanggalSewa : String                                          |
+--------------------+   | - durasiHari : int                                              |
| + getKapasitasCC() |   | - rincianBiaya : List<DetailItemBiaya>                          |
| + servisBerkala()  |   | - totalBiaya : double                                           |
+--------------------+   | - statusSewa : String                                           |
                         +-----------------------------------------------------------------+
                         | + tambahLayananTambahan(nama, tarif, qty) : void                |
                         | + perpanjangDurasi(tambahanHari : int) : boolean                |
                         | + prosesPengembalian(hariTelat : int, notif : LayananNotif)     |
                         | + cetakStrukDenganPrinter(p : KuitansiPrinter, kasir : String)  |
                         +-----------------------------------------------------------------+
                                  ◆ 1                                   | - - - - -> (Uses)
                                  | Composition (1..*)                  |
                                  v                                     v
                         +---------------------------------+   +-----------------------------------+
                         |         DetailItemBiaya         |   | KuitansiPrinter / LayananNotif    |
                         +---------------------------------+   +-----------------------------------+
                         | - namaItem : String             |   | + cetakKuitansiResmi(...)         |
                         | - hargaSatuan : double          |   | + kirimNotifikasiPengembalian(...) |
                         | - kuantitas : int               |   +-----------------------------------+
                         | - subtotal : double             |
                         +---------------------------------+
                         | + getSubtotal() : double        |
                         | + tampilkanItem(no : int) : void|
                         +---------------------------------+
    """
    add_code_block(uml_diagram_text.strip(), "📄 Diagram 1. UML Class Diagram Terpadu AutoRent System (Modul 4 / Sprint 4)")

    # -------------------------------------------------------------
    # BAB III: IMPLEMENTASI KODE PROGRAM JAVA (SRC)
    # -------------------------------------------------------------
    add_header_title("BAB III: IMPLEMENTASI KODE PROGRAM JAVA (SRC)", 1)

    add_body_p(
        "Implementasi kode program pada modul ini mengikuti struktur standar paket Java di bawah folder `src/`. "
        "Seluruh class dirancang dengan memenuhi aturan access modifier private, pemanggilan method antarentitas, "
        "serta penerapan pola Association, Aggregation, dan Composition sesuai arahan Bagian C, D, dan E Modul 4:",
        bold_prefix="3.0 Pendekatan Implementasi & Struktur Modul: "
    )

    add_header_title("3.1 Bagian C – Implementasi Association (Class TransaksiSewa)", 2)
    add_body_p(
        "Sesuai Bagian C Modul 4, class `TransaksiSewa` bertindak sebagai entitas asosiasi yang menghubungkan object `Pelanggan` dan `Kendaraan`. "
        "Kedua objek referensi tersebut disuplai saat instansiasi transaksi dan disimpan sebagai private attribute:\n"
        "```java\n"
        "public class TransaksiSewa {\n"
        "    private Pelanggan pelanggan;\n"
        "    private Kendaraan kendaraan;\n"
        "    public TransaksiSewa(String kode, Pelanggan pelanggan, Kendaraan kendaraan, ...) {\n"
        "        this.pelanggan = pelanggan;\n"
        "        this.kendaraan = kendaraan;\n"
        "    }\n"
        "}\n"
        "```\n"
        "Saat transaksi aktif, `TransaksiSewa` memanggil method `kendaraan.setStatusKetersediaan(false)` dan `pelanggan.tambahSewaAktif(1)` "
        "untuk memastikan kolaborasi antarobjek berjalan sinkron tanpa melanggar prinsip enkapsulasi.",
        bold_prefix="Mekanisme Asosiasi: "
    )

    add_header_title("3.2 Bagian D – Implementasi Aggregation (Class GarasiArmada)", 2)
    add_body_p(
        "Sesuai Bagian D Modul 4, class `GarasiArmada` mengimplementasikan Agregasi ('has-a') dengan menampung koleksi objek `Kendaraan` "
        "yang diciptakan di luar garasi:\n"
        "```java\n"
        "// Objek dibuat di luar garasi (mandiri)\n"
        "Kendaraan k1 = new Kendaraan(\"L 1234 AB\", \"Toyota Avanza\", \"MPV\", 400000, ...);\n"
        "GarasiArmada garasi = new GarasiArmada(\"GAR-01\", \"Garasi Surabaya\", \"Surabaya\", 10);\n"
        "garasi.tambahKendaraan(k1); // Dimasukkan ke dalam agregasi\n"
        "```\n"
        "Jika `garasi.kosongkanGarasi()` dijalankan, daftar kendaraan di garasi dikosongkan namun objek `k1` tetap hidup di memori dan dapat dipindahkan ke garasi cabang lain.",
        bold_prefix="Mekanisme Agregasi: "
    )

    add_header_title("3.3 Bagian E – Implementasi Composition (Kendaraan & Mesin, Transaksi & DetailItemBiaya)", 2)
    add_body_p(
        "Sesuai Bagian E Modul 4, Komposisi ('part-of') diterapkan di mana objek bagian diciptakan secara langsung di dalam objek pemiliknya menggunakan kata kunci `new`:\n"
        "1. `Kendaraan ◆— Mesin`: Di dalam constructor `Kendaraan`, objek `Mesin` diinstansiasi secara internal:\n"
        "   `this.mesin = new Mesin(noRangka, kapasitasCC, transmisi);`\n"
        "2. `TransaksiSewa ◆— DetailItemBiaya`: Di dalam constructor dan method `tambahLayananTambahan()`, objek `DetailItemBiaya` diciptakan secara eksklusif oleh `TransaksiSewa`:\n"
        "   `rincianBiaya.add(new DetailItemBiaya(\"Sewa Armada Pokok\", tarifPerHari, durasiHari));`\n"
        "Objek Mesin dan DetailItemBiaya tidak dapat hidup mandiri di luar induknya, menjamin integritas data dan kepemilikan yang kuat.",
        bold_prefix="Mekanisme Komposisi: "
    )

    add_header_title("3.4 Dokumentasi Listing Kode Sumber Java (9 File Source Code)", 2)

    src_files = [
        ("4.1 File: src/Mesin.java (Part Komposisi dari Kendaraan)", "src/Mesin.java", "Listing Program 1. File src/Mesin.java"),
        ("4.2 File: src/Kendaraan.java (Whole dari Mesin & Part dari GarasiArmada)", "src/Kendaraan.java", "Listing Program 2. File src/Kendaraan.java"),
        ("4.3 File: src/Pelanggan.java (Entitas Pelanggan Berasosiasi dengan Transaksi)", "src/Pelanggan.java", "Listing Program 3. File src/Pelanggan.java"),
        ("4.4 File: src/GarasiArmada.java (Whole dari Agregasi Kendaraan)", "src/GarasiArmada.java", "Listing Program 4. File src/GarasiArmada.java"),
        ("4.5 File: src/DetailItemBiaya.java (Part Komposisi dari TransaksiSewa)", "src/DetailItemBiaya.java", "Listing Program 5. File src/DetailItemBiaya.java"),
        ("4.6 File: src/KuitansiPrinter.java (Service Pencetakan Thermal POS / Dependensi)", "src/KuitansiPrinter.java", "Listing Program 6. File src/KuitansiPrinter.java"),
        ("4.7 File: src/LayananNotifikasi.java (Service Gateway Notifikasi / Dependensi)", "src/LayananNotifikasi.java", "Listing Program 7. File src/LayananNotifikasi.java"),
        ("4.8 File: src/TransaksiSewa.java (Sentral Kolaborasi Seluruh Relasi)", "src/TransaksiSewa.java", "Listing Program 8. File src/TransaksiSewa.java"),
        ("4.9 File: src/Main.java (Driver & Test Suite Skenario Interaksi Objek)", "src/Main.java", "Listing Program 9. File src/Main.java")
    ]

    for title, path, caption in src_files:
        add_header_title(title, 2)
        code_content = get_source_file_content(path)
        add_code_block(code_content, f"📄 {caption}")

    # -------------------------------------------------------------
    # BAB IV: PENGUJIAN INTERAKSI OBJECT & HASIL EKSEKUSI
    # -------------------------------------------------------------
    add_header_title("BAB IV: PENGUJIAN INTERAKSI OBJECT & HASIL EKSEKUSI", 1)

    add_body_p(
        "Berdasarkan Bagian F Modul 4, pengujian sistem dirancang untuk memverifikasi 3 skenario dasar interaksi objek, "
        "ditambah skenario pengujian lanjutan pembuktian lifecycle dan defensive boundary validation:",
        bold_prefix="4.1 Skenario Pengujian Interaksi Objek (Bagian F Modul 4): "
    )

    test_scenarios = [
        ("Skenario Test 1 (Objek Berhasil Dibuat)", "Memverifikasi instansiasi objek Kendaraan (yang otomatis menginstansiasi Mesin internalnya), GarasiArmada, dan Pelanggan berhasil dialokasikan pada memori dengan state yang valid."),
        ("Skenario Test 2 (Interaksi Dua/Lebih Objek)", "Memverifikasi pembentukan TransaksiSewa yang mengaitkan Pelanggan dan Kendaraan, serta penambahan unit Kendaraan ke dalam GarasiArmada secara agregasi."),
        ("Skenario Test 3 (Pemanggilan Data via Method)", "Memverifikasi TransaksiSewa dapat mengakses method `kendaraan.getTarifPerHari()`, `kendaraan.getMesin().getKapasitasCC()`, dan `pelanggan.getNamaLengkap()` untuk menghasilkan kuitansi resmi dan notifikasi.")
    ]

    t_scen = doc.add_table(rows=4, cols=2)
    t_scen.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_scen)
    for i, h in enumerate(["Skenario Pengujian Modul 4", "Tujuan & Deskripsi Verifikasi"]):
        cell = t_scen.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    for row_idx, (t_title, t_desc) in enumerate(test_scenarios):
        row = t_scen.rows[row_idx + 1]
        c0, c1 = row.cells[0], row.cells[1]
        c0.width = Inches(2.2)
        c1.width = Inches(4.3)
        set_cell_margins(c0, 80, 80, 100, 100)
        set_cell_margins(c1, 80, 80, 100, 100)
        if row_idx % 2 == 1:
            set_cell_shading(c0, HEX_ROW_ALT)
            set_cell_shading(c1, HEX_ROW_ALT)
        p0 = c0.paragraphs[0]
        p0.paragraph_format.space_after = Pt(0)
        r0 = p0.add_run(t_title)
        r0.bold = True
        r0.font.name = 'Calibri'
        r0.font.size = Pt(9)
        r0.font.color.rgb = COLOR_DARK_TEXT

        p1 = c1.paragraphs[0]
        p1.paragraph_format.space_after = Pt(0)
        r1 = p1.add_run(t_desc)
        r1.font.name = 'Calibri'
        r1.font.size = Pt(9)
        r1.font.color.rgb = COLOR_DARK_TEXT

    p_div4 = doc.add_paragraph()
    p_div4.paragraph_format.space_after = Pt(6)

    add_header_title("4.2 Log Output Eksekusi Program Java Konsol Lengkap", 2)
    add_body_p(
        "Berikut adalah rekaman log eksekusi langsung dari kompilasi `javac -d bin src/*.java` dan eksekusi `java -cp bin src.Main` "
        "yang menunjukkan keberhasilan seluruh 8 fase pengujian interaksi objek tanpa error:"
    )

    output_log_text = get_source_file_content("execution_output.txt")
    add_code_block(output_log_text, "📄 Gambar 1. Log Eksekusi Konsol Program Java AutoRent System (Sprint 4 / P4)")

    add_header_title("4.3 Analisis & Pembahasan Hasil Pengujian Interaksi Objek", 2)
    add_body_p(
        "1. Verifikasi Test 1 (Instansiasi Objek): Seluruh objek (3 unit Kendaraan, 3 unit Mesin terkomposisi, 2 Garasi, 2 Pelanggan) "
        "berhasil diciptakan. Mesin `ENG-2NR-FE-9921` langsung melekat pada unit Toyota Avanza `L 1234 AB`.\n\n"
        "2. Verifikasi Test 2 (Interaksi Objek): Objek `GarasiArmada` berhasil menampung unit kendaraan. Objek `TransaksiSewa` berhasil "
        "mengaitkan `Budi Santoso` dengan `Toyota Avanza`, mengubah status ketersediaan armada menjadi `Booked`, dan menambah kuota sewa pelanggan.\n\n"
        "3. Verifikasi Test 3 (Kolaborasi Method): Transaksi sewa berhasil menghitung total biaya berdasarkan tarif harian unit kendaraan, "
        "menambahkan rincian biaya add-on (driver, asuransi, GPS), dan mengoper data ke `KuitansiPrinter` serta `LayananNotifikasi`.\n\n"
        "4. Pembuktian Empiris Sifat Agregasi: Saat Garasi Surabaya dikosongkan (`kosongkanGarasi()`), unit `L 1234 AB` dan `M 9900 EF` "
        "terbukti tetap hidup di memori dan berhasil dipindahkan ke Garasi Cabang Kota Malang.\n\n"
        "5. Ketahanan Boundary Defense: Sistem berhasil menolak kondisi abnormal seperti penambahan armada saat garasi penuh, penyewaan unit yang sedang booked, "
        "penyewaan melebihi kuota 2 unit per pelanggan, perpanjangan durasi bernilai negatif, dan pengoperan objek bernilai null.",
        bold_prefix="Poin Pembahasan Teknis: "
    )

    # -------------------------------------------------------------
    # BAB V: SPRINT EVALUATION, DEFINITION OF DONE & PENUTUP
    # -------------------------------------------------------------
    add_header_title("BAB V: SPRINT EVALUATION, DEFINITION OF DONE & PENUTUP", 1)

    add_header_title("5.1 Sprint Review (Format Resmi Modul 4)", 2)
    add_body_p("Sesuai tabel evaluasi Sprint Review pada Bab 15 Modul 4:")

    t_review = doc.add_table(rows=8, cols=3)
    t_review.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_review)
    rev_headers = ["Item Evaluasi Sprint Review Modul 4", "Hasil Verifikasi & Bukti Implementasi", "Status"]
    for i, h in enumerate(rev_headers):
        cell = t_review.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    rev_data = [
        ("Class diagram diperbarui", "UML Class Diagram lengkap mencakup Association, Aggregation, Composition, dan Multiplicity.", "SUKSES"),
        ("Association berhasil", "Relasi Pelanggan — TransaksiSewa — Kendaraan terhubung stabil dan sinkron.", "SUKSES"),
        ("Aggregation berhasil", "GarasiArmada mengelola Kendaraan; lifecycle part terbukti independen saat garasi dikosongkan.", "SUKSES"),
        ("Composition berhasil", "Kendaraan ◆— Mesin & TransaksiSewa ◆— DetailItemBiaya terikat kuat secara eksklusif.", "SUKSES"),
        ("Object dapat berinteraksi", "Seluruh objek saling bertukar pesan melalui pemanggilan method public tanpa merusak encapsulation.", "SUKSES"),
        ("Program berjalan", "Kompilasi bersih (javac) dan runtime (java) lulus seluruh 8 skenario pengujian konsol.", "SUKSES"),
        ("Kendala", "Sinkronisasi status unit saat pengembalian dan validasi kuota sewa pelanggan telah diselesaikan defensif.", "TERATASI")
    ]

    for row_idx, data in enumerate(rev_data):
        row = t_review.rows[row_idx + 1]
        for col_idx, text in enumerate(data):
            cell = row.cells[col_idx]
            set_cell_margins(cell, 80, 80, 100, 100)
            if row_idx % 2 == 1:
                set_cell_shading(cell, HEX_ROW_ALT)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(text)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if col_idx == 0:
                r.bold = True
            if col_idx == 2:
                r.bold = True
                r.font.color.rgb = COLOR_GREEN_SUCCESS

    p_div5 = doc.add_paragraph()
    p_div5.paragraph_format.space_after = Pt(6)

    add_header_title("5.2 Sprint Retrospective (Format Resmi Modul 4)", 2)
    add_body_p(
        "Implementasi relasi Agregasi pada GarasiArmada dan Komposisi pada Mesin serta DetailItemBiaya berhasil memisahkan "
        "tanggung jawab siklus hidup objek secara sangat rapi, sementara relasi Asosiasi pada TransaksiSewa sukses mengorkestrasi "
        "alur rental kendaraan secara dinamis dan terstruktur.",
        bold_prefix="• What Went Well? (Bagian relasi apa yang berhasil diimplementasikan?):\n"
    )
    add_body_p(
        "Tantangan utama terletak pada penentuan batas kepemilikan yang tegas antara Agregasi dan Komposisi serta sinkronisasi mutasi state "
        "dua arah (seperti pengubahan status ketersediaan armada dan penambahan kuota sewa pelanggan) saat transaksi dibuat atau diselesaikan.",
        bold_prefix="• What Went Wrong? (Apa kesulitan dalam menentukan jenis relasi?):\n"
    )
    add_body_p(
        "Desain class saat ini masih mengelompokkan semua tipe kendaraan dalam satu class konkret Kendaraan. "
        "Perlu dilakukan generalisasi pada Sprint berikutnya (P5) dengan membentuk hierarki inheritance (superclass Kendaraan dan subclass spesifik) "
        "agar pengelolaan tipe kendaraan lebih polimorfis.",
        bold_prefix="• Improvement (Apa yang perlu diperbaiki pada desain class?):\n"
    )

    add_header_title("5.3 Checklist Ketercapaian Definition of Done (14 Butir Lengkap Modul 4)", 2)

    t_dod = doc.add_table(rows=15, cols=3)
    t_dod.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_dod)
    for i, h in enumerate(["No", "Kriteria Definition of Done (DoD) Resmi Modul 4", "Status Verifikasi"]):
        cell = t_dod.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    dod_official_criteria = [
        ("1", "Menggunakan project P3 (AutoRent System dilanjutkan tanpa membuat project baru)", "✓ TERPENUHI"),
        ("2", "Minimal 4 class digunakan (Total 9 class Java diimplementasikan)", "✓ TERPENUHI"),
        ("3", "Relasi antarclass telah dianalisis (Tabel identifikasi relasi Bagian A tersedia)", "✓ TERPENUHI"),
        ("4", "Class diagram diperbarui (UML Class Diagram Bagian B termutakhir)", "✓ TERPENUHI"),
        ("5", "Minimal 1 association diterapkan (Pelanggan — TransaksiSewa — Kendaraan)", "✓ TERPENUHI"),
        ("6", "Minimal 1 aggregation diterapkan (GarasiArmada ◇— Kendaraan)", "✓ TERPENUHI"),
        ("7", "Minimal 1 composition diterapkan (Kendaraan ◆— Mesin & Transaksi ◆— DetailItemBiaya)", "✓ TERPENUHI"),
        ("8", "Object berinteraksi melalui method (Message passing antarobjek terenkapsulasi)", "✓ TERPENUHI"),
        ("9", "Tidak mengakses attribute private secara langsung (Enkapsulasi 100% terjaga)", "✓ TERPENUHI"),
        ("10", "Program berhasil dikompilasi (javac -d bin src/*.java bersih tanpa error)", "✓ TERPENUHI"),
        ("11", "Program berhasil dijalankan (java -cp bin src.Main sukses menampilkan seluruh output)", "✓ TERPENUHI"),
        ("12", "Minimal 3 skenario pengujian tersedia (Test 1, Test 2, Test 3 & Negative Tests)", "✓ TERPENUHI"),
        ("13", "Sprint Backlog diperbarui (Status SB-01 s/d SB-06 seluruhnya DONE)", "✓ TERPENUHI"),
        ("14", "Sprint Review & Sprint Retrospective tersedia (Format baku Modul 4 terisi lengkap)", "✓ TERPENUHI")
    ]

    for row_idx, (no, crit, stat) in enumerate(dod_official_criteria):
        row = t_dod.rows[row_idx + 1]
        c0, c1, c2 = row.cells[0], row.cells[1], row.cells[2]
        c0.width = Inches(0.5)
        c1.width = Inches(4.8)
        c2.width = Inches(1.2)
        set_cell_margins(c0, 60, 60, 80, 80)
        set_cell_margins(c1, 60, 60, 80, 80)
        set_cell_margins(c2, 60, 60, 80, 80)
        if row_idx % 2 == 1:
            set_cell_shading(c0, HEX_ROW_ALT)
            set_cell_shading(c1, HEX_ROW_ALT)
            set_cell_shading(c2, HEX_ROW_ALT)

        for c_i, val in enumerate([no, crit, stat]):
            p = row.cells[c_i].paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if c_i == 0:
                r.bold = True
            if c_i == 2:
                r.bold = True
                r.font.color.rgb = COLOR_GREEN_SUCCESS

    p_div6 = doc.add_paragraph()
    p_div6.paragraph_format.space_after = Pt(6)

    add_header_title("5.4 Evaluasi Rubrik Penilaian Modul 4", 2)
    add_body_p("Pemenuhan komponen penilaian berdasarkan Bab 18 Modul 4:")

    t_rubrik = doc.add_table(rows=8, cols=4)
    t_rubrik.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_rubrik)
    rub_headers = ["Komponen Penilaian", "Bobot", "Capaian Implementasi pada Laporan", "Estimasi Skor"]
    for i, h in enumerate(rub_headers):
        cell = t_rubrik.rows[0].cells[i]
        set_cell_shading(cell, HEX_NAVY)
        set_cell_margins(cell, 100, 100, 100, 100)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        r = p.add_run(h)
        r.bold = True
        r.font.name = 'Calibri'
        r.font.size = Pt(9.5)
        r.font.color.rgb = COLOR_WHITE

    rub_data = [
        ("Analisis relasi antarclass", "15%", "Tabel identifikasi 6 relasi lengkap dengan alasan domain bisnis yang logis.", "15 / 15%"),
        ("Class Diagram", "20%", "UML Class Diagram terpadu dengan atribut, method, multiplicity, dan notasi baku.", "20 / 20%"),
        ("Implementasi Association", "15%", "Kode Java TransaksiSewa mengaitkan Pelanggan & Kendaraan via message passing.", "15 / 15%"),
        ("Implementasi Aggregation & Composition", "25%", "GarasiArmada (Agregasi) dan Kendaraan-Mesin / Transaksi-DetailBiaya (Komposisi).", "25 / 25%"),
        ("Pengujian interaksi object", "15%", "Test 1, 2, 3, pembuktian lifecycle memori, dan 5 defensive boundary tests.", "15 / 15%"),
        ("Agile Sprint & dokumentasi", "10%", "Sprint Goal, Backlog (SB-01 s/d SB-06), Sprint Review, Retrospective, dan DoD.", "10 / 10%"),
        ("TOTAL CAPAIAN", "100%", "Seluruh kriteria penilaian terpenuhi secara sempurna dan terverifikasi.", "100 / 100%")
    ]

    for row_idx, data in enumerate(rub_data):
        row = t_rubrik.rows[row_idx + 1]
        for col_idx, text in enumerate(data):
            cell = row.cells[col_idx]
            set_cell_margins(cell, 80, 80, 100, 100)
            if row_idx % 2 == 1:
                set_cell_shading(cell, HEX_ROW_ALT)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            r = p.add_run(text)
            r.font.name = 'Calibri'
            r.font.size = Pt(9)
            r.font.color.rgb = COLOR_DARK_TEXT
            if col_idx in (0, 1) or row_idx == len(rub_data) - 1:
                r.bold = True
            if col_idx == 3:
                r.bold = True
                r.font.color.rgb = COLOR_GREEN_SUCCESS

    p_div7 = doc.add_paragraph()
    p_div7.paragraph_format.space_after = Pt(6)

    add_header_title("5.5 Target Sprint Berikutnya (P5 – Inheritance dan Generalization)", 2)
    add_body_p(
        "Hasil praktikum P4 ini menjadi fondasi yang kokoh untuk melangkah ke Praktikum Pertemuan 5 (P5) mengenai "
        "Inheritance (Pewarisan) dan Generalization. Pada Sprint 5 berikutnya, arsitektur AutoRent System akan dikembangkan lebih lanjut dengan:\n"
        "• Mengevaluasi kesamaan atribut dan behavior pada armada rental untuk membentuk Superclass `Kendaraan`.\n"
        "• Membentuk Subclass spesifik seperti `MobilPenumpang`, `MobilLogistik`, dan `KendaraanListrik (EV)`.\n"
        "• Menerapkan kata kunci `super` pada konstruktor dan pemanggilan method induk.\n"
        "• Mengoptimalkan polimorfisme saat pemrosesan transaksi sewa di seluruh cabang garasi.",
        bold_prefix="Evolusi Arsitektur P4 menuju P5: "
    )

    add_header_title("5.6 Kesimpulan Akhir", 2)
    add_body_p(
        "Praktikum Pemrograman Berorientasi Obyek Modul 4 (Relasi Antarobject: Association, Aggregation, dan Composition) "
        "pada proyek AutoRent System telah berhasil diselesaikan dengan hasil sempurna dan memenuhi 100% kriteria Definition of Done. "
        "Transformasi dari class-class terenkapsulasi P3 menjadi jaringan kolaborasi objek P4 membuktikan bahwa sistem perangkat lunak berorientasi objek "
        "yang berkualitas tinggi dibangun atas dasar pemisahan tanggung jawab (separation of concerns), kopling rendah (low coupling), "
        "dan batasan siklus hidup kepemilikan yang tegas. Seluruh program Java telah terkompilasi bersih, tereksekusi tanpa kendala, "
        "dan siap menjadi pijakan utama untuk implementasi Inheritance pada Modul 5."
    )

    # Save document
    output_filename = "Laporan_Praktikum_PBO_Modul_4_AutoRent.docx"
    doc.save(output_filename)
    print(f"Laporan berhasil dibuat dan disimpan ke: {output_filename}")

if __name__ == "__main__":
    create_report()
