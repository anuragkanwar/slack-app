import {  clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'
import type {ClassValue} from 'clsx';

export function cn(...inputs: Array<ClassValue>) {
  return twMerge(clsx(inputs))
}

export const formDataToObj = (formData: FormData): Record<string, any> => {
  const obj: Record<string, any> = {};
  formData.forEach((v, k) => {
    if (!(k in obj)) {
      obj[k] = v;
    }
  });
  return obj;
};