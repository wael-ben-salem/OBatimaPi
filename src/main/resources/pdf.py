import mysql.connector
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Image
from reportlab.lib import colors
from reportlab.lib.units import inch
import tkinter as tk
from tkinter import filedialog
from PIL import Image as PILImage
from io import BytesIO

DB_CONFIG = {
    'host': 'localhost',
    'database': 'OurBatimapi',
    'user': 'root',
    'password': ''
}

def get_articles():
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor(dictionary=True)

    query = """
    SELECT
        article.nom,
        article.description,
        article.prix_unitaire,
        article.photo,
        stock.nom as stock_nom,
        fournisseur.nom as fournisseur_nom,
        etapeprojet.nomEtape as etapeprojet_nom
    FROM article
    LEFT JOIN stock ON article.stock_id = stock.id
    LEFT JOIN fournisseur ON article.fournisseur_id = fournisseur.fournisseur_id
    LEFT JOIN etapeprojet ON article.id_etapeProjet = etapeprojet.Id_etapeProjet
    """

    cursor.execute(query)
    results = cursor.fetchall()
    conn.close()
    return results

def create_pdf(output_path, data):
    doc = SimpleDocTemplate(output_path, pagesize=letter)
    elements = []

    # Prepare table data
    pdf_data = [["Photo", "Nom", "Description", "Prix Unitaire", "Stock", "Fournisseur", "Etape Projet"]]

    for article in data:
        # Process image
        img_flowable = None
        try:
            pil_img = PILImage.open(article['photo'])
            pil_img.thumbnail((50, 50))  # Resize image
            img_buffer = BytesIO()
            pil_img.save(img_buffer, format='JPEG')
            img_buffer.seek(0)
            img_flowable = Image(img_buffer, width=1*inch, height=1*inch)
        except:
            img_flowable = "Image non trouvée"

        # Convert prix_unitaire to float and format it
        try:
            prix_unitaire = float(article['prix_unitaire'])
            prix_formatted = f"{prix_unitaire:.2f} €"
        except (ValueError, TypeError):
            prix_formatted = "N/A"

        row = [
            img_flowable,
            article['nom'],
            article['description'],
            prix_formatted,
            article['stock_nom'],
            article['fournisseur_nom'],
            article['etapeprojet_nom']
        ]
        pdf_data.append(row)

    # Create table
    table = Table(pdf_data)
    style = TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.grey),
        ('TEXTCOLOR', (0,0), (-1,0), colors.whitesmoke),
        ('ALIGN', (0,0), (-1,-1), 'CENTER'),
        ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'),
        ('FONTSIZE', (0,0), (-1,0), 10),
        ('BOTTOMPADDING', (0,0), (-1,0), 12),
        ('BACKGROUND', (0,1), (-1,-1), colors.beige),
        ('GRID', (0,0), (-1,-1), 1, colors.black),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ])

    # Adjust column widths
    table._argW = [1.2*inch] + [0.8*inch]*6

    table.setStyle(style)
    elements.append(table)
    doc.build(elements)

def main():
    # Get save path
    root = tk.Tk()
    root.withdraw()
    save_path = filedialog.asksaveasfilename(
        defaultextension=".pdf",
        filetypes=[("PDF Files", "*.pdf")],
        title="Enregistrer le PDF"
    )

    if not save_path:
        print("Annulé par l'utilisateur")
        return

    # Get data and generate PDF
    try:
        articles = get_articles()
        create_pdf(save_path, articles)
        print(f"PDF généré avec succès à : {save_path}")
    except Exception as e:
        print(f"Erreur : {str(e)}")

if __name__ == "__main__":
    main()