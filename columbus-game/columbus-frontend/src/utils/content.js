import { z } from "zod";

const contentSchema = z.object({
  grid: z.array(z.array(z.string())).optional(),
  logs: z.array(z.string()).optional(),
});

export function parseContent(data) {
  if (!data || typeof data !== 'object') {
    console.error('Invalid content data:', data);
    return {};
  }

  try {
    return contentSchema.parse(data);
  } catch (error) {
    console.error('Zod parsing error:', error);
    return {};
  }
}
