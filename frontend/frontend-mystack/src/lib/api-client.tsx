import axios from "axios"

export const apiClient = axios.create({
  baseURL : "http://localhost:8080/api/",
  withCredentials: true
})


export const attachCookie = (
  cookie?: string,
  headers?: Record<string, string>
) => {
  return {
    headers: {
      ...headers,
      ...(cookie ? { Cookie: cookie } : {})
    }
  };
};