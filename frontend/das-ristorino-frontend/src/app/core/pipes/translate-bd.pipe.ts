import { Pipe, PipeTransform, inject } from '@angular/core';
import { LOCALE_ID } from '@angular/core';

/**
 * Pipe para traducir valores que vienen de la base de datos.
 * Mapea los valores de BD a sus traducciones según el idioma actual.
 */
@Pipe({
  name: 'translateBd',
  standalone: true
})
export class TranslateBdPipe implements PipeTransform {

  private readonly locale = inject(LOCALE_ID);

  // Mapeo completo de traducciones
  private readonly translations: { [key: string]: { 'es-AR': string; 'en': string } } = {
    // Categorías
    'Ambiente': { 'es-AR': 'Ambiente', 'en': 'Ambience' },
    'Ambience': { 'es-AR': 'Ambiente', 'en': 'Ambience' },
    'Rango Precio': { 'es-AR': 'Rango Precio', 'en': 'Price Range' },
    'Rango de precio': { 'es-AR': 'Rango Precio', 'en': 'Price Range' },
    'Price Range': { 'es-AR': 'Rango Precio', 'en': 'Price Range' },
    'Tipo de Comida': { 'es-AR': 'Tipo de Comida', 'en': 'Food Type' },
    'Tipo de comida': { 'es-AR': 'Tipo de Comida', 'en': 'Food Type' },
    'Food Type': { 'es-AR': 'Tipo de Comida', 'en': 'Food Type' },
    
    // Dominios - Ambiente
    'Familiar': { 'es-AR': 'Familiar', 'en': 'Family' },
    'Family': { 'es-AR': 'Familiar', 'en': 'Family' },
    'Romántico': { 'es-AR': 'Romántico', 'en': 'Romantic' },
    'Romantic': { 'es-AR': 'Romántico', 'en': 'Romantic' },
    'Casual': { 'es-AR': 'Casual', 'en': 'Casual' },
    'Formal': { 'es-AR': 'Formal', 'en': 'Formal' },
    'Rústico': { 'es-AR': 'Rústico', 'en': 'Rustic' },
    'Rustic': { 'es-AR': 'Rústico', 'en': 'Rustic' },
    'Moderno': { 'es-AR': 'Moderno', 'en': 'Modern' },
    'Modern': { 'es-AR': 'Moderno', 'en': 'Modern' },
    'Temático': { 'es-AR': 'Temático', 'en': 'Themed' },
    'Themed': { 'es-AR': 'Temático', 'en': 'Themed' },
    'Deportivo': { 'es-AR': 'Deportivo', 'en': 'Sports' },
    'Sports': { 'es-AR': 'Deportivo', 'en': 'Sports' },
    'Elegante': { 'es-AR': 'Elegante', 'en': 'Elegant' },
    'Elegant': { 'es-AR': 'Elegante', 'en': 'Elegant' },
    
    // Rango Precio
    'Económico': { 'es-AR': 'Económico', 'en': 'Economy' },
    'Economy': { 'es-AR': 'Económico', 'en': 'Economy' },
    'Moderado': { 'es-AR': 'Moderado', 'en': 'Moderate' },
    'Moderate': { 'es-AR': 'Moderado', 'en': 'Moderate' },
    'Medio': { 'es-AR': 'Medio', 'en': 'Medium' },
    'Medium': { 'es-AR': 'Medio', 'en': 'Medium' },
    'Alto': { 'es-AR': 'Alto', 'en': 'High' },
    'High': { 'es-AR': 'Alto', 'en': 'High' },
    'Muy Alto': { 'es-AR': 'Muy Alto', 'en': 'Very High' },
    'Very High': { 'es-AR': 'Muy Alto', 'en': 'Very High' },
    'Premium': { 'es-AR': 'Premium', 'en': 'Premium' },
    
    // Tipo de Comida
    'Italiana': { 'es-AR': 'Italiana', 'en': 'Italian' },
    'Italian': { 'es-AR': 'Italiana', 'en': 'Italian' },
    'Argentina': { 'es-AR': 'Argentina', 'en': 'Argentine' },
    'Argentine': { 'es-AR': 'Argentina', 'en': 'Argentine' },
    'Mexicana': { 'es-AR': 'Mexicana', 'en': 'Mexican' },
    'Mexican': { 'es-AR': 'Mexicana', 'en': 'Mexican' },
    'Japonesa': { 'es-AR': 'Japonesa', 'en': 'Japanese' },
    'Japanese': { 'es-AR': 'Japonesa', 'en': 'Japanese' },
    'China': { 'es-AR': 'China', 'en': 'Chinese' },
    'Chinese': { 'es-AR': 'China', 'en': 'Chinese' },
    'Asiática': { 'es-AR': 'Asiática', 'en': 'Asian' },
    'Asian': { 'es-AR': 'Asiática', 'en': 'Asian' },
    'Vegetariana': { 'es-AR': 'Vegetariana', 'en': 'Vegetarian' },
    'Vegetarian': { 'es-AR': 'Vegetariana', 'en': 'Vegetarian' },
    'Vegana': { 'es-AR': 'Vegana', 'en': 'Vegan' },
    'Vegano': { 'es-AR': 'Vegano', 'en': 'Vegan' },
    'Vegan': { 'es-AR': 'Vegano', 'en': 'Vegan' },
    'Sin TACC': { 'es-AR': 'Sin TACC', 'en': 'Gluten Free' },
    'Gluten Free': { 'es-AR': 'Sin TACC', 'en': 'Gluten Free' },
    'Mariscos': { 'es-AR': 'Mariscos', 'en': 'Seafood' },
    'Seafood': { 'es-AR': 'Mariscos', 'en': 'Seafood' },
    'Carnes': { 'es-AR': 'Carnes', 'en': 'Meat' },
    'Meat': { 'es-AR': 'Carnes', 'en': 'Meat' },
    'Parrilla': { 'es-AR': 'Parrilla', 'en': 'Grill' },
    'Grill': { 'es-AR': 'Parrilla', 'en': 'Grill' },
    'Pizza': { 'es-AR': 'Pizza', 'en': 'Pizza' },
    'Pizzería': { 'es-AR': 'Pizzería', 'en': 'Pizzeria' },
    'Pizzeria': { 'es-AR': 'Pizzería', 'en': 'Pizzeria' },
    'Sushi': { 'es-AR': 'Sushi', 'en': 'Sushi' },
    'Fast Food': { 'es-AR': 'Fast Food', 'en': 'Fast Food' },
    'Gourmet': { 'es-AR': 'Gourmet', 'en': 'Gourmet' }
  };

  transform(value: string): string {
    if (!value) {
      return value;
    }

    const lang = this.locale.startsWith('es') ? 'es-AR' : 'en';
    const translation = this.translations[value];
    
    return translation ? translation[lang] : value;
  }
}

