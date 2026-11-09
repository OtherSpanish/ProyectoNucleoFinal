import { Pipe, PipeTransform } from '@angular/core';

/** 7 → "07:00" */
@Pipe({ name: 'hora', standalone: false })
export class HoraPipe implements PipeTransform {
  transform(hora: number | null | undefined): string {
    if (hora === null || hora === undefined) {
      return '—';
    }
    return `${String(hora).padStart(2, '0')}:00`;
  }
}
