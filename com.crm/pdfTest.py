import psycopg2

# Connection details based on your shell environment
DB_HOST = "localhost"
DB_PORT = "7614"
DB_NAME = "crmdb"
DB_USER = "dbuser"
DB_PASS = "dbuser"


def extract_pdf():
  try:
    print("[Python] Connecting to database...")
    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASS,
    )
    cur = conn.cursor()

    # Query the PDF for your test record (ID 5001)
    print("[Python] Fetching pdf_file for ID 5001...")
    cur.execute("SELECT pdf_file FROM insurance_files WHERE id = 5001;")
    row = cur.fetchone()

    if row and row[0]:
      pdf_bytes = row[0]
      output_path = "test_insurance_output.pdf"

      # Write raw bytes to a local PDF file
      with open(output_path, "wb") as f:
        f.write(pdf_bytes)

      print(
          f"[Python] Success! PDF saved locally as '{output_path}'"
          f" ({len(pdf_bytes)} bytes)."
      )
    else:
      print("[Python] Error: No PDF found for ID 5001 or the field is null.")

    cur.close()
    conn.close()

  except Exception as e:
    print(f"[Python] Database error: {e}")


if __name__ == "__main__":
  extract_pdf()
